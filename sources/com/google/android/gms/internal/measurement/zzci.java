package com.google.android.gms.internal.measurement;

import android.database.ContentObserver;
import android.os.Handler;

/* compiled from: com.google.android.gms:play-services-measurement-impl@@17.0.1 */
final class zzci extends ContentObserver {
    zzci(zzcg zzcg, Handler handler) {
        super(null);
    }

    public final void onChange(boolean z) {
        zzcl.zza();
    }
}