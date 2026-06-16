package net.paulem.vanillahammers.langs;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationStore;
import net.kyori.adventure.translation.Translator;
import net.paulem.vanillahammers.VanillaHammers;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.StreamSupport;

public class LangsManager {
    private LangsManager() {
        /* This utility class should not be instantiated */
    }

    public static void init() {
        List<TranslationStore.StringBased<MessageFormat>> stores = List.of(
                registerLocale(Locale.US),
                registerLocale(Locale.ENGLISH),
                registerLocale(Locale.FRANCE)
        );

        for (TranslationStore.StringBased<MessageFormat> store : stores) {
            GlobalTranslator.translator().addSource(store);
        }

        VanillaHammers.INSTANCE.getLogger().info("Initialized translations !");
    }

    public static TranslationStore.StringBased<MessageFormat> registerLocale(Locale locale) {
        TranslationStore.StringBased<MessageFormat> store = TranslationStore.messageFormat(VanillaHammers.key(locale.getLanguage()));
        ResourceBundle bundle = ResourceBundle.getBundle("vanilla-hammers.translations", locale);
        store.registerAll(locale, bundle, true);

        return store;
    }

    public static @Nullable Component translated(String key, Locale locale) {
        // Get the translator for the current locale
        @Nullable
        Translator localeTranslator = StreamSupport.stream(GlobalTranslator.translator().sources().spliterator(), false)
                .filter(translator -> translator.canTranslate(key, locale))
                .findFirst()
                .orElse(null);

        if(localeTranslator == null) {
            if(locale == Locale.US) return null;

            return translated(key, Locale.US);
        }

        MessageFormat translated = localeTranslator.translate(key, locale);
        if(translated == null) {
            if(locale == Locale.US) return null;

            return translated(key, Locale.US);
        }

        return Component.text(translated.toPattern());
    }
}
