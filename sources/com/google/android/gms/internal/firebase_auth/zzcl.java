package com.google.android.gms.internal.firebase_auth;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Class;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Constructor;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Field;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Param;

@Class(creator = "LinkEmailAuthCredentialAidlRequestCreator")
/* compiled from: com.google.firebase:firebase-auth@@18.1.0 */
public final class zzcl extends AbstractSafeParcelable {
    public static final Creator<zzcl> CREATOR = new zzco();
    @Field(getter = "getEmail", mo15127id = 1)
    private final String zza;
    @Field(getter = "getPassword", mo15127id = 2)
    private final String zzb;
    @Field(getter = "getCachedState", mo15127id = 3)
    private final String zzc;

    @Constructor
    public zzcl(@Param(mo15130id = 1) String str, @Param(mo15130id = 2) String str2, @Param(mo15130id = 3) String str3) {
        this.zza = str;
        this.zzb = str2;
        this.zzc = str3;
    }

    public final String zza() {
        return this.zza;
    }

    public final String zzb() {
        return this.zzb;
    }

    public final String zzc() {
        return this.zzc;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(parcel);
        SafeParcelWriter.writeString(parcel, 1, this.zza, false);
        SafeParcelWriter.writeString(parcel, 2, this.zzb, false);
        SafeParcelWriter.writeString(parcel, 3, this.zzc, false);
        SafeParcelWriter.finishObjectHeader(parcel, beginObjectHeader);
    }
}