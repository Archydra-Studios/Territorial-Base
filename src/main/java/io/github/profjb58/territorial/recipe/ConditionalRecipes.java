package io.github.profjb58.territorial.recipe;

import io.github.profjb58.territorial.event.registry.TerritorialRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

public class ConditionalRecipes {
    public static class OmniscientObsidian extends ShapelessRecipe {
        private static final DefaultedList<Ingredient> input;
        private static final ItemStack output = new ItemStack(TerritorialRegistry.OMNISCIENT_OBSIDIAN);

        public OmniscientObsidian(Identifier id) {
            super(id, "null", output, input);
        }

        static {
            input = DefaultedList.of();
            input.add(Ingredient.ofItems(Items.CRYING_OBSIDIAN));
            input.add(Ingredient.ofItems(Items.ENDER_EYE));
        }
    }
}
