package p006io.grpc.okhttp.internal.framed;

import com.facebook.common.util.UriUtil;
import com.google.common.primitives.SignedBytes;
import com.google.common.primitives.UnsignedBytes;
import com.google.firebase.analytics.FirebaseAnalytics.Param;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;
import okio.Okio;
import okio.Source;
import p006io.grpc.internal.GrpcUtil;

/* renamed from: io.grpc.okhttp.internal.framed.Hpack */
final class Hpack {
    /* access modifiers changed from: private */
    public static final Map<ByteString, Integer> NAME_TO_FIRST_INDEX = nameToFirstIndex();
    private static final int PREFIX_4_BITS = 15;
    private static final int PREFIX_5_BITS = 31;
    private static final int PREFIX_6_BITS = 63;
    private static final int PREFIX_7_BITS = 127;
    /* access modifiers changed from: private */
    public static final Header[] STATIC_HEADER_TABLE;

    /* renamed from: io.grpc.okhttp.internal.framed.Hpack$Reader */
    static final class Reader {
        Header[] dynamicTable = new Header[8];
        int dynamicTableByteCount = 0;
        int dynamicTableHeaderCount = 0;
        private final List<Header> headerList = new ArrayList();
        private int headerTableSizeSetting;
        private int maxDynamicTableByteCount;
        int nextDynamicTableIndex = (this.dynamicTable.length - 1);
        private final BufferedSource source;

        Reader(int i, Source source2) {
            this.headerTableSizeSetting = i;
            this.maxDynamicTableByteCount = i;
            this.source = Okio.buffer(source2);
        }

        /* access modifiers changed from: 0000 */
        public int maxDynamicTableByteCount() {
            return this.maxDynamicTableByteCount;
        }

        /* access modifiers changed from: 0000 */
        public void headerTableSizeSetting(int i) {
            this.headerTableSizeSetting = i;
            this.maxDynamicTableByteCount = i;
            adjustDynamicTableByteCount();
        }

        private void adjustDynamicTableByteCount() {
            int i = this.maxDynamicTableByteCount;
            int i2 = this.dynamicTableByteCount;
            if (i >= i2) {
                return;
            }
            if (i == 0) {
                clearDynamicTable();
            } else {
                evictToRecoverBytes(i2 - i);
            }
        }

        private void clearDynamicTable() {
            Arrays.fill(this.dynamicTable, null);
            this.nextDynamicTableIndex = this.dynamicTable.length - 1;
            this.dynamicTableHeaderCount = 0;
            this.dynamicTableByteCount = 0;
        }

        private int evictToRecoverBytes(int i) {
            int i2 = 0;
            if (i > 0) {
                int length = this.dynamicTable.length;
                while (true) {
                    length--;
                    if (length < this.nextDynamicTableIndex || i <= 0) {
                        Header[] headerArr = this.dynamicTable;
                        int i3 = this.nextDynamicTableIndex;
                        System.arraycopy(headerArr, i3 + 1, headerArr, i3 + 1 + i2, this.dynamicTableHeaderCount);
                        this.nextDynamicTableIndex += i2;
                    } else {
                        i -= this.dynamicTable[length].hpackSize;
                        this.dynamicTableByteCount -= this.dynamicTable[length].hpackSize;
                        this.dynamicTableHeaderCount--;
                        i2++;
                    }
                }
                Header[] headerArr2 = this.dynamicTable;
                int i32 = this.nextDynamicTableIndex;
                System.arraycopy(headerArr2, i32 + 1, headerArr2, i32 + 1 + i2, this.dynamicTableHeaderCount);
                this.nextDynamicTableIndex += i2;
            }
            return i2;
        }

        /* access modifiers changed from: 0000 */
        public void readHeaders() throws IOException {
            while (!this.source.exhausted()) {
                byte readByte = this.source.readByte() & UnsignedBytes.MAX_VALUE;
                if (readByte == 128) {
                    throw new IOException("index == 0");
                } else if ((readByte & UnsignedBytes.MAX_POWER_OF_TWO) == 128) {
                    readIndexedHeader(readInt(readByte, Hpack.PREFIX_7_BITS) - 1);
                } else if (readByte == 64) {
                    readLiteralHeaderWithIncrementalIndexingNewName();
                } else if ((readByte & SignedBytes.MAX_POWER_OF_TWO) == 64) {
                    readLiteralHeaderWithIncrementalIndexingIndexedName(readInt(readByte, 63) - 1);
                } else if ((readByte & 32) == 32) {
                    this.maxDynamicTableByteCount = readInt(readByte, 31);
                    int i = this.maxDynamicTableByteCount;
                    if (i < 0 || i > this.headerTableSizeSetting) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Invalid dynamic table size update ");
                        sb.append(this.maxDynamicTableByteCount);
                        throw new IOException(sb.toString());
                    }
                    adjustDynamicTableByteCount();
                } else if (readByte == 16 || readByte == 0) {
                    readLiteralHeaderWithoutIndexingNewName();
                } else {
                    readLiteralHeaderWithoutIndexingIndexedName(readInt(readByte, 15) - 1);
                }
            }
        }

        public List<Header> getAndResetHeaderList() {
            ArrayList arrayList = new ArrayList(this.headerList);
            this.headerList.clear();
            return arrayList;
        }

        private void readIndexedHeader(int i) throws IOException {
            if (isStaticHeader(i)) {
                this.headerList.add(Hpack.STATIC_HEADER_TABLE[i]);
                return;
            }
            int dynamicTableIndex = dynamicTableIndex(i - Hpack.STATIC_HEADER_TABLE.length);
            if (dynamicTableIndex >= 0) {
                Header[] headerArr = this.dynamicTable;
                if (dynamicTableIndex <= headerArr.length - 1) {
                    this.headerList.add(headerArr[dynamicTableIndex]);
                    return;
                }
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Header index too large ");
            sb.append(i + 1);
            throw new IOException(sb.toString());
        }

        private int dynamicTableIndex(int i) {
            return this.nextDynamicTableIndex + 1 + i;
        }

        private void readLiteralHeaderWithoutIndexingIndexedName(int i) throws IOException {
            this.headerList.add(new Header(getName(i), readByteString()));
        }

        private void readLiteralHeaderWithoutIndexingNewName() throws IOException {
            this.headerList.add(new Header(Hpack.checkLowercase(readByteString()), readByteString()));
        }

        private void readLiteralHeaderWithIncrementalIndexingIndexedName(int i) throws IOException {
            insertIntoDynamicTable(-1, new Header(getName(i), readByteString()));
        }

        private void readLiteralHeaderWithIncrementalIndexingNewName() throws IOException {
            insertIntoDynamicTable(-1, new Header(Hpack.checkLowercase(readByteString()), readByteString()));
        }

        private ByteString getName(int i) {
            if (isStaticHeader(i)) {
                return Hpack.STATIC_HEADER_TABLE[i].name;
            }
            return this.dynamicTable[dynamicTableIndex(i - Hpack.STATIC_HEADER_TABLE.length)].name;
        }

        private boolean isStaticHeader(int i) {
            return i >= 0 && i <= Hpack.STATIC_HEADER_TABLE.length - 1;
        }

        private void insertIntoDynamicTable(int i, Header header) {
            this.headerList.add(header);
            int i2 = header.hpackSize;
            if (i != -1) {
                i2 -= this.dynamicTable[dynamicTableIndex(i)].hpackSize;
            }
            int i3 = this.maxDynamicTableByteCount;
            if (i2 > i3) {
                clearDynamicTable();
                return;
            }
            int evictToRecoverBytes = evictToRecoverBytes((this.dynamicTableByteCount + i2) - i3);
            if (i == -1) {
                int i4 = this.dynamicTableHeaderCount + 1;
                Header[] headerArr = this.dynamicTable;
                if (i4 > headerArr.length) {
                    Header[] headerArr2 = new Header[(headerArr.length * 2)];
                    System.arraycopy(headerArr, 0, headerArr2, headerArr.length, headerArr.length);
                    this.nextDynamicTableIndex = this.dynamicTable.length - 1;
                    this.dynamicTable = headerArr2;
                }
                int i5 = this.nextDynamicTableIndex;
                this.nextDynamicTableIndex = i5 - 1;
                this.dynamicTable[i5] = header;
                this.dynamicTableHeaderCount++;
            } else {
                this.dynamicTable[i + dynamicTableIndex(i) + evictToRecoverBytes] = header;
            }
            this.dynamicTableByteCount += i2;
        }

        private int readByte() throws IOException {
            return this.source.readByte() & UnsignedBytes.MAX_VALUE;
        }

        /* access modifiers changed from: 0000 */
        public int readInt(int i, int i2) throws IOException {
            int i3 = i & i2;
            if (i3 < i2) {
                return i3;
            }
            int i4 = 0;
            while (true) {
                int readByte = readByte();
                if ((readByte & 128) == 0) {
                    return i2 + (readByte << i4);
                }
                i2 += (readByte & Hpack.PREFIX_7_BITS) << i4;
                i4 += 7;
            }
        }

        /* access modifiers changed from: 0000 */
        public ByteString readByteString() throws IOException {
            int readByte = readByte();
            boolean z = (readByte & 128) == 128;
            int readInt = readInt(readByte, Hpack.PREFIX_7_BITS);
            if (z) {
                return ByteString.m398of(Huffman.get().decode(this.source.readByteArray((long) readInt)));
            }
            return this.source.readByteString((long) readInt);
        }
    }

    /* renamed from: io.grpc.okhttp.internal.framed.Hpack$Writer */
    static final class Writer {
        private final Buffer out;

        Writer(Buffer buffer) {
            this.out = buffer;
        }

        /* access modifiers changed from: 0000 */
        public void writeHeaders(List<Header> list) throws IOException {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                ByteString asciiLowercase = ((Header) list.get(i)).name.toAsciiLowercase();
                Integer num = (Integer) Hpack.NAME_TO_FIRST_INDEX.get(asciiLowercase);
                if (num != null) {
                    writeInt(num.intValue() + 1, 15, 0);
                    writeByteString(((Header) list.get(i)).value);
                } else {
                    this.out.writeByte(0);
                    writeByteString(asciiLowercase);
                    writeByteString(((Header) list.get(i)).value);
                }
            }
        }

        /* access modifiers changed from: 0000 */
        public void writeInt(int i, int i2, int i3) throws IOException {
            if (i < i2) {
                this.out.writeByte(i | i3);
                return;
            }
            this.out.writeByte(i3 | i2);
            int i4 = i - i2;
            while (i4 >= 128) {
                this.out.writeByte(128 | (i4 & Hpack.PREFIX_7_BITS));
                i4 >>>= 7;
            }
            this.out.writeByte(i4);
        }

        /* access modifiers changed from: 0000 */
        public void writeByteString(ByteString byteString) throws IOException {
            writeInt(byteString.size(), Hpack.PREFIX_7_BITS, 0);
            this.out.write(byteString);
        }
    }

    static {
        String str = "";
        STATIC_HEADER_TABLE = new Header[]{new Header(Header.TARGET_AUTHORITY, str), new Header(Header.TARGET_METHOD, "GET"), new Header(Header.TARGET_METHOD, GrpcUtil.HTTP_METHOD), new Header(Header.TARGET_PATH, "/"), new Header(Header.TARGET_PATH, "/index.html"), new Header(Header.TARGET_SCHEME, UriUtil.HTTP_SCHEME), new Header(Header.TARGET_SCHEME, UriUtil.HTTPS_SCHEME), new Header(Header.RESPONSE_STATUS, "200"), new Header(Header.RESPONSE_STATUS, "204"), new Header(Header.RESPONSE_STATUS, "206"), new Header(Header.RESPONSE_STATUS, "304"), new Header(Header.RESPONSE_STATUS, "400"), new Header(Header.RESPONSE_STATUS, "404"), new Header(Header.RESPONSE_STATUS, "500"), new Header("accept-charset", str), new Header(GrpcUtil.CONTENT_ACCEPT_ENCODING, "gzip, deflate"), new Header("accept-language", str), new Header("accept-ranges", str), new Header("accept", str), new Header("access-control-allow-origin", str), new Header("age", str), new Header("allow", str), new Header("authorization", str), new Header("cache-control", str), new Header("content-disposition", str), new Header(GrpcUtil.CONTENT_ENCODING, str), new Header("content-language", str), new Header("content-length", str), new Header("content-location", str), new Header("content-range", str), new Header("content-type", str), new Header("cookie", str), new Header("date", str), new Header("etag", str), new Header("expect", str), new Header("expires", str), new Header("from", str), new Header("host", str), new Header("if-match", str), new Header("if-modified-since", str), new Header("if-none-match", str), new Header("if-range", str), new Header("if-unmodified-since", str), new Header("last-modified", str), new Header("link", str), new Header(Param.LOCATION, str), new Header("max-forwards", str), new Header("proxy-authenticate", str), new Header("proxy-authorization", str), new Header("range", str), new Header("referer", str), new Header("refresh", str), new Header("retry-after", str), new Header("server", str), new Header("set-cookie", str), new Header("strict-transport-security", str), new Header("transfer-encoding", str), new Header("user-agent", str), new Header("vary", str), new Header("via", str), new Header("www-authenticate", str)};
    }

    private Hpack() {
    }

    private static Map<ByteString, Integer> nameToFirstIndex() {
        LinkedHashMap linkedHashMap = new LinkedHashMap(STATIC_HEADER_TABLE.length);
        int i = 0;
        while (true) {
            Header[] headerArr = STATIC_HEADER_TABLE;
            if (i >= headerArr.length) {
                return Collections.unmodifiableMap(linkedHashMap);
            }
            if (!linkedHashMap.containsKey(headerArr[i].name)) {
                linkedHashMap.put(STATIC_HEADER_TABLE[i].name, Integer.valueOf(i));
            }
            i++;
        }
    }

    /* access modifiers changed from: private */
    public static ByteString checkLowercase(ByteString byteString) throws IOException {
        int size = byteString.size();
        int i = 0;
        while (i < size) {
            byte b = byteString.getByte(i);
            if (b < 65 || b > 90) {
                i++;
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("PROTOCOL_ERROR response malformed: mixed case name: ");
                sb.append(byteString.utf8());
                throw new IOException(sb.toString());
            }
        }
        return byteString;
    }
}