package p006io.sentry.event;

import androidx.core.app.NotificationCompat;
import com.facebook.common.util.UriUtil;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/* renamed from: io.sentry.event.Breadcrumb */
public class Breadcrumb implements Serializable {
    private final String category;
    private final Map<String, String> data;
    private final Level level;
    private final String message;
    private final Date timestamp;
    private final Type type;

    /* renamed from: io.sentry.event.Breadcrumb$Level */
    public enum Level {
        DEBUG("debug"),
        INFO("info"),
        WARNING("warning"),
        ERROR("error"),
        CRITICAL("critical");
        
        private final String value;

        private Level(String str) {
            this.value = str;
        }

        public String getValue() {
            return this.value;
        }
    }

    /* renamed from: io.sentry.event.Breadcrumb$Type */
    public enum Type {
        DEFAULT("default"),
        HTTP(UriUtil.HTTP_SCHEME),
        NAVIGATION(NotificationCompat.CATEGORY_NAVIGATION),
        USER("user");
        
        private final String value;

        private Type(String str) {
            this.value = str;
        }

        public String getValue() {
            return this.value;
        }
    }

    Breadcrumb(Type type2, Date date, Level level2, String str, String str2, Map<String, String> map) {
        if (date == null) {
            date = new Date();
        }
        if (str != null || (map != null && map.size() >= 1)) {
            this.type = type2;
            this.timestamp = date;
            this.level = level2;
            this.message = str;
            this.category = str2;
            this.data = map;
            return;
        }
        throw new IllegalArgumentException("one of 'message' or 'data' must be set");
    }

    public Type getType() {
        return this.type;
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public Level getLevel() {
        return this.level;
    }

    public String getMessage() {
        return this.message;
    }

    public String getCategory() {
        return this.category;
    }

    public Map<String, String> getData() {
        return this.data;
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Breadcrumb breadcrumb = (Breadcrumb) obj;
        if (this.type != breadcrumb.type || !Objects.equals(this.timestamp, breadcrumb.timestamp) || this.level != breadcrumb.level || !Objects.equals(this.message, breadcrumb.message) || !Objects.equals(this.category, breadcrumb.category) || !Objects.equals(this.data, breadcrumb.data)) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.type, this.timestamp, this.level, this.message, this.category, this.data});
    }
}
