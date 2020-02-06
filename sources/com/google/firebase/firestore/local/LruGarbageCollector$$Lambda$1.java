package com.google.firebase.firestore.local;

import com.google.firebase.firestore.util.Consumer;

/* compiled from: com.google.firebase:firebase-firestore@@20.2.0 */
final /* synthetic */ class LruGarbageCollector$$Lambda$1 implements Consumer {
    private final RollingSequenceNumberBuffer arg$1;

    private LruGarbageCollector$$Lambda$1(RollingSequenceNumberBuffer rollingSequenceNumberBuffer) {
        this.arg$1 = rollingSequenceNumberBuffer;
    }

    public static Consumer lambdaFactory$(RollingSequenceNumberBuffer rollingSequenceNumberBuffer) {
        return new LruGarbageCollector$$Lambda$1(rollingSequenceNumberBuffer);
    }

    public void accept(Object obj) {
        this.arg$1.addElement(Long.valueOf(((QueryData) obj).getSequenceNumber()));
    }
}