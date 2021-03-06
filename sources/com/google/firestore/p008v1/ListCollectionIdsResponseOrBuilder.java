package com.google.firestore.p008v1;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageLiteOrBuilder;
import java.util.List;

/* renamed from: com.google.firestore.v1.ListCollectionIdsResponseOrBuilder */
/* compiled from: com.google.firebase:firebase-firestore@@20.2.0 */
public interface ListCollectionIdsResponseOrBuilder extends MessageLiteOrBuilder {
    String getCollectionIds(int i);

    ByteString getCollectionIdsBytes(int i);

    int getCollectionIdsCount();

    List<String> getCollectionIdsList();

    String getNextPageToken();

    ByteString getNextPageTokenBytes();
}
