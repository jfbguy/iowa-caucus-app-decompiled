package com.drew.metadata;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;

public class Face {
    @Nullable
    private final Age _age;
    private final int _height;
    @Nullable
    private final String _name;
    private final int _width;

    /* renamed from: _x */
    private final int f24_x;

    /* renamed from: _y */
    private final int f25_y;

    public Face(int i, int i2, int i3, int i4, @Nullable String str, @Nullable Age age) {
        this.f24_x = i;
        this.f25_y = i2;
        this._width = i3;
        this._height = i4;
        this._name = str;
        this._age = age;
    }

    public int getX() {
        return this.f24_x;
    }

    public int getY() {
        return this.f25_y;
    }

    public int getWidth() {
        return this._width;
    }

    public int getHeight() {
        return this._height;
    }

    @Nullable
    public String getName() {
        return this._name;
    }

    @Nullable
    public Age getAge() {
        return this._age;
    }

    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Face face = (Face) obj;
        if (this._height != face._height || this._width != face._width || this.f24_x != face.f24_x || this.f25_y != face.f25_y) {
            return false;
        }
        Age age = this._age;
        if (age == null ? face._age != null : !age.equals(face._age)) {
            return false;
        }
        String str = this._name;
        return str == null ? face._name == null : str.equals(face._name);
    }

    public int hashCode() {
        int i = ((((((this.f24_x * 31) + this.f25_y) * 31) + this._width) * 31) + this._height) * 31;
        String str = this._name;
        int i2 = 0;
        int hashCode = (i + (str != null ? str.hashCode() : 0)) * 31;
        Age age = this._age;
        if (age != null) {
            i2 = age.hashCode();
        }
        return hashCode + i2;
    }

    @NotNull
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("x: ");
        sb.append(this.f24_x);
        sb.append(" y: ");
        sb.append(this.f25_y);
        sb.append(" width: ");
        sb.append(this._width);
        sb.append(" height: ");
        sb.append(this._height);
        if (this._name != null) {
            sb.append(" name: ");
            sb.append(this._name);
        }
        if (this._age != null) {
            sb.append(" age: ");
            sb.append(this._age.toFriendlyString());
        }
        return sb.toString();
    }
}
