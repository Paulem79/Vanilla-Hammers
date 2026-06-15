package net.paulem.vanillahammers.langs;

import net.kyori.adventure.translation.AbstractTranslationStore;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationStore;
import net.kyori.examination.string.StringExaminer;
import net.paulem.vanillahammers.VanillaHammers;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class LangsManager {
    private LangsManager() {
        /* This utility class should not be instantiated */
    }

    public static void init() {
        List<TranslationStore.StringBased<MessageFormat>> stores = List.of(
                registerLocale(Locale.US),
                registerLocale(Locale.FRANCE)
        );

        for (TranslationStore.StringBased<MessageFormat> store : stores) {
            GlobalTranslator.translator().addSource(store);
        }

        VanillaHammers.INSTANCE.getLogger().info("Initialized translations !");
    }

    public static TranslationStore.StringBased<MessageFormat> registerLocale(Locale locale) {
        TranslationStore.StringBased<MessageFormat> store = TranslationStore.messageFormat(VanillaHammers.key("vanillahammers"));
        ResourceBundle bundle = ResourceBundle.getBundle("vanilla-hammers.translations", locale);
        store.registerAll(locale, bundle, true);

        AbstractTranslationStore<MessageFormat> abstractStore = (AbstractTranslationStore<MessageFormat>) store;
        abstractStore.examinableProperties().forEach(examinableProperty -> {
            System.out.println(examinableProperty.examine(StringExaminer.simpleEscaping()));
        });

        return store;
    }
}
