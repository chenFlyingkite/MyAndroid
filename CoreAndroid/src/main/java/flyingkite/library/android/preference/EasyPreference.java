package flyingkite.library.android.preference;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;

import java.util.Map;

import flyingkite.library.android.log.Loggable;

public class EasyPreference implements Loggable {
    public final SharedPreferences preference;

    public EasyPreference(Context context, String name) {
        preference = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    private SharedPreferences.Editor edit() {
        return preference.edit();
    }

    // -- Basic operations --
    public void remove(String key) {
        edit().remove(key).apply();
    }
    public boolean contains(String key) {
        return preference.contains(key);
    }

    // -- String Preference --
    // Use StringPref instead
    public String getString(String key) {
        return getString(key, "");
    }
    public String getString(String key, String defValue) {
        return preference.getString(key, defValue);
    }
    public void putString(String key, String value) {
        edit().putString(key, value).apply();
    }

    // -- Boolean Preference --
    // Use BoolPref instead
    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }
    public boolean getBoolean(String key, boolean defValue) {
        return preference.getBoolean(key, defValue);
    }
    public void putBoolean(String key, boolean value) {
        edit().putBoolean(key, value).apply();
    }

    // -- Integer Preference --
    // Use IntPref instead
    public int getInt(String key) {
        return getInt(key, 0);
    }
    public int getInt(String key, int defValue) {
        return preference.getInt(key, defValue);
    }
    public void putInt(String key, int value) {
        edit().putInt(key, value).apply();
    }

    // -- Long Preference --
    // Use LongPref instead
    public long getLong(String key) {
        return preference.getLong(key, 0L);
    }
    public long getLong(String key, long defValue) {
        return preference.getLong(key, defValue);
    }
    public void putLong(String key, long value) {
        edit().putLong(key, value).apply();
    }

    // Public classes for easy use of preferences fields

    public class BoolPref {
        public final String key;
        private boolean def;
        public BoolPref(String _key) {
            this(_key, false);
        }

        public BoolPref(String _key, boolean defValue) {
            key = _key;
            def = defValue;
        }

        public boolean get() {
            return get(def);
        }

        public boolean get(boolean b) {
            return getBoolean(key, b);
        }

        public void set(boolean b) {
            putBoolean(key, b);
        }

        @NonNull
        @Override
        public String toString() {
            return key + " -> " + get();
        }
    }

    public class IntPref {
        public final String key;
        private int def;
        public IntPref(String _key) {
            this(_key, 0);
        }

        public IntPref(String _key, int defValue) {
            key = _key;
            def = defValue;
        }

        public int get() {
            return get(def);
        }

        public int get(int b) {
            return getInt(key, b);
        }

        public void set(int b) {
            putInt(key, b);
        }

        // Set and add
        public void add(int b) {
            set(get() + b);
        }

        @NonNull
        @Override
        public String toString() {
            return key + " -> " + get();
        }
    }

    public class LongPref {
        public final String key;
        private long def;
        public LongPref(String _key) {
            this(_key, 0L);
        }

        public LongPref(String _key, long defValue) {
            key = _key;
            def = defValue;
        }

        public long get() {
            return get(def);
        }

        public long get(long v) {
            return getLong(key, v);
        }

        public void set(long v) {
            putLong(key, v);
        }

        // Set and add
        public void add(int b) {
            set(get() + b);
        }

        @NonNull
        @Override
        public String toString() {
            return key + " -> " + get();
        }
    }

    public class StringPref {
        public final String key;
        private String def;
        public StringPref(String _key) {
            this(_key, "");
        }

        public StringPref(String _key, String defValue) {
            key = _key;
            def = defValue;
        }

        public String get() {
            return get(def);
        }

        public String get(String s) {
            return getString(key, s);
        }

        public void set(String s) {
            putString(key, s);
        }

        // Set and add
        public void add(String b) {
            set(get() + b);
        }

        @NonNull
        @Override
        public String toString() {
            return key + " -> " + get();
        }
    }

    public abstract class TypePref<T> {
        public final String key;
        private T def;
        public TypePref(String _key) {
            this(_key, null);
        }

        public TypePref(String _key, T defValue) {
            key = _key;
            def = defValue;
        }

        public T get() {
            return get(def);
        }

        public abstract T get(T s);

        public abstract void set(T s);

        @NonNull
        @Override
        public String toString() {
            return key + " -> " + get();
        }
    }

    public void printAll() {
        logE("\n---- Pref.%s ----", LTag());
        Map<String, ?> m = preference.getAll();
        for (String s : m.keySet()) {
            logE("%s -> %s", s, m.get(s));
        }
        logE("-------\n");
    }
}
