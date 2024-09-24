package airdnb.docs.common;

import static org.springframework.restdocs.snippet.Attributes.key;

import org.springframework.restdocs.snippet.Attributes.Attribute;

public interface DateTimeFormat {

    static Attribute getDateTimeFormat() {
        return key("format").value("yyyy-MM-dd HH:mm");
    }

    static Attribute getDateFormat() {
        return key("format").value("yyyy-MM-dd");
    }

    static Attribute getTimeFormat() {
        return key("format").value("HH:mm:ss");
    }
}
