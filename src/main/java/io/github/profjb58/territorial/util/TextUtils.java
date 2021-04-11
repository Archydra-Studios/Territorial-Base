package io.github.profjb58.territorial.util;

import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.List;

public class TextUtils {

    public static LiteralText spacer() {
        return new LiteralText(" ");
    }

    public static class ToolTip {

        public static void addMultilineText(List<Text> tooltip, String translatableTextId, int numCycles) {
            addMultilineText(tooltip, translatableTextId, numCycles, 0);
        }
        public static void addMultilineText(List<Text> tooltip, String translatableTextId, int numCycles, int startIndex) {
            for(int i=startIndex; i < (startIndex + numCycles); i++) {
                tooltip.add(new TranslatableText(translatableTextId + "_" + i));
            }
        }
    }
}
