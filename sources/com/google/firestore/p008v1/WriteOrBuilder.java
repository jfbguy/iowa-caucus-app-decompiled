package com.google.firestore.p008v1;

import com.google.firestore.p008v1.Write.OperationCase;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageLiteOrBuilder;

/* renamed from: com.google.firestore.v1.WriteOrBuilder */
/* compiled from: com.google.firebase:firebase-firestore@@20.2.0 */
public interface WriteOrBuilder extends MessageLiteOrBuilder {
    Precondition getCurrentDocument();

    String getDelete();

    ByteString getDeleteBytes();

    OperationCase getOperationCase();

    DocumentTransform getTransform();

    Document getUpdate();

    DocumentMask getUpdateMask();

    boolean hasCurrentDocument();

    boolean hasUpdateMask();
}
