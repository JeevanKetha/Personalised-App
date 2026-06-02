package com.example.data.nutrition

import java.util.Locale

data class IndianFoodEntry(
    val name: String,
    val caloriesPer100g: Double,
    val proteinPer100g: Double,
    val carbsPer100g: Double,
    val fatPer100g: Double,
    val category: String,
    val digestion: String = "2 Hours",
    val profile: String = "Sustained Energy Release"
)

object IndianFoodNutritionDb {
    val foods = listOf(
        // BREAKFAST / SOUTH INDIAN
        IndianFoodEntry("Idli", 112.0, 3.2, 23.3, 0.4, "South Indian Breakfast", "1.5 Hours", "Clean Fermented Complex Carbs"),
        IndianFoodEntry("Dosa Plain", 168.0, 3.9, 29.0, 3.7, "South Indian Breakfast", "2 Hours", "Moderate Glycemic Energy"),
        IndianFoodEntry("Masala Dosa", 210.0, 4.2, 32.0, 7.5, "South Indian Breakfast", "2.5 Hours", "Sustained Starchy Energy"),
        IndianFoodEntry("Rava Dosa", 180.0, 4.0, 31.0, 4.5, "South Indian Breakfast", "2 Hours", "Moderate Glycemic Release"),
        IndianFoodEntry("Onion Uttapam", 155.0, 3.5, 26.0, 4.0, "South Indian Breakfast", "2 Hours", "Fermented Veggie Sustained"),
        IndianFoodEntry("Medu Vada", 335.0, 11.5, 34.0, 17.0, "Fried Breakfast Snack", "3 Hours", "Slow Digesting Fat & Starch"),
        IndianFoodEntry("Idiyappam", 105.0, 2.0, 24.0, 0.2, "South Indian Breakfast", "1.5 Hours", "Clean Steamed Rice Carbs"),
        IndianFoodEntry("Puttu", 240.0, 5.0, 48.0, 4.0, "South Indian Breakfast", "2 Hours", "Dense Rice & Grass Carbs"),
        IndianFoodEntry("Appam", 120.0, 2.2, 24.0, 1.5, "South Indian Breakfast", "1.5 Hours", "Steamed Slow Release"),
        
        // BREAKFAST / GENERAL
        IndianFoodEntry("Poha", 180.0, 3.1, 35.0, 2.5, "Breakfast", "1.5 Hours", "Light Carb Quick Release"),
        IndianFoodEntry("Upma", 160.0, 3.5, 29.0, 3.5, "Breakfast", "2 Hours", "Semolina Morning Sustained"),
        IndianFoodEntry("Rava Khichdi", 150.0, 3.0, 27.0, 3.2, "Breakfast", "2 Hours", "Balanced Semolina & Moong"),
        IndianFoodEntry("Shira", 250.0, 2.5, 45.0, 7.0, "Sweet Breakfast", "2 Hours", "Fast Glycemic Energy"),
        IndianFoodEntry("Dhokla", 160.0, 6.0, 24.0, 4.0, "Gujarati Breakfast", "1.5 Hours", "High Biocompatibility Protein & Fermented Carbs"),
        IndianFoodEntry("Thepla", 280.0, 6.5, 42.0, 9.5, "Gujarati Breakfast", "2 Hours", "Spiced Flatbread Fibre"),
        IndianFoodEntry("Khandvi", 140.0, 4.5, 17.0, 6.0, "Gujarati Breakfast", "1.5 Hours", "Gram Flour Low Calories"),
        IndianFoodEntry("Handvo", 195.0, 7.0, 29.0, 5.2, "Gujarati Breakfast", "2.5 Hours", "Lentil & Vegetable Dense Recovery"),
        IndianFoodEntry("Sabudana Khichdi", 320.0, 1.5, 68.0, 5.0, "Fasting Breakfast", "1.5 Hours", "High Glycemic Quick Energy"),
        IndianFoodEntry("Aloo Paratha", 210.0, 4.5, 33.0, 6.5, "North Indian Breakfast", "2.5 Hours", "Starchy Heavy Fuel"),
        IndianFoodEntry("Paneer Paratha", 265.0, 10.0, 28.0, 12.0, "North Indian Breakfast", "3 Hours", "Deep Nitrogen Balance & Sustained Release"),
        IndianFoodEntry("GobiParatha", 185.0, 4.2, 28.0, 6.0, "North Indian Breakfast", "2 Hours", "Fibre Rich Complex Carb"),
        IndianFoodEntry("Methi Paratha", 190.0, 5.0, 29.0, 6.2, "North Indian Breakfast", "2 Hours", "Herbal Blood Sugar Friendly Flatbread"),
        IndianFoodEntry("Chole Bhature", 350.0, 8.5, 45.0, 15.0, "Heavy Breakfast", "3.5 Hours", "Very Slow Deep Fry & Legume Digestion"),
        IndianFoodEntry("Puri Bhaji", 290.0, 4.2, 38.0, 13.0, "Heavy Breakfast", "3 Hours", "Starchy Deep Fried Fast Release"),
        
        // GRAINS & BREADS
        IndianFoodEntry("Cooked Rice", 130.0, 2.7, 28.0, 0.3, "Rice", "1.5 Hours", "Rapid Muscle Glycogen Replenishment"),
        IndianFoodEntry("Brown Rice", 111.0, 2.6, 23.0, 0.9, "Rice", "2 Hours", "Clean Low-GI Fiber Fuel"),
        IndianFoodEntry("Roti Plain", 260.0, 9.0, 55.0, 1.5, "Flatbread", "2 Hours", "Standard Complex Carbohydrates"),
        IndianFoodEntry("Chapati Plain", 265.0, 8.5, 54.0, 1.6, "Flatbread", "2 Hours", "Whole Wheat sustained energy"),
        IndianFoodEntry("Butter Naan", 320.0, 9.0, 50.0, 9.0, "Indian Flatbread", "3 Hours", "High Fat Fuel"),
        IndianFoodEntry("Plain Naan", 280.0, 8.5, 52.0, 3.5, "Indian Flatbread", "2.5 Hours", "Yeast Fermented Rapid Carbs"),
        IndianFoodEntry("Rumali Roti", 250.0, 7.5, 49.0, 2.5, "Indian Flatbread", "1.5 Hours", "Thin Wheat Quick Digestion"),
        IndianFoodEntry("Garlic Naan", 310.0, 8.2, 51.0, 7.5, "Indian Flatbread", "2.5 Hours", "Aromatic Spiced Wheat Fuel"),
        IndianFoodEntry("Kerala Parotta", 340.0, 7.0, 52.0, 11.5, "Indian Flatbread", "3 Hours", "Heavy Layered Refined Flour"),
        IndianFoodEntry("Ragi Roti", 210.0, 4.8, 44.0, 1.4, "High-Calcium Flatbread", "2.5 Hours", "Extremely Low GI Bone Strength Synthesis"),
        IndianFoodEntry("Jowar Bhakri", 230.0, 6.0, 48.0, 1.8, "High-Fiber Flatbread", "2.5 Hours", "Gluten-Free Slow Hydrolysis Fuel"),
        IndianFoodEntry("Bajra Roti", 240.0, 7.0, 49.0, 2.2, "Flatbread", "2.5 Hours", "Warmth Inducing Slow Carbs"),
        
        // IN-BETWEEN RICE MIXES / BIRYANI
        IndianFoodEntry("Veg Biryani", 160.0, 3.8, 28.0, 4.0, "Rice Dish", "2.5 Hours", "Rich Spiced Complex Carb"),
        IndianFoodEntry("Chicken Biryani", 195.0, 11.2, 26.0, 5.8, "Rice Dish", "3 Hours", "High-Protein Muscle Support Fuel"),
        IndianFoodEntry("Egg Biryani", 175.0, 7.5, 27.0, 4.5, "Rice Dish", "2.5 Hours", "Balanced Protein & Rice"),
        IndianFoodEntry("Paneer Biryani", 185.0, 8.0, 27.0, 6.5, "Rice Dish", "3 Hours", "Anabolic Sustained Release"),
        IndianFoodEntry("Jeera Rice", 140.0, 2.8, 29.0, 1.5, "Rice Dish", "1.5 Hours", "Cumin Aided Quick Digest Carbon"),
        IndianFoodEntry("Veg Pulao", 145.0, 3.2, 29.0, 2.2, "Rice Dish", "2 Hours", "Veggie Enriched Rice Grid"),
        IndianFoodEntry("Khichdi Plain", 115.0, 3.5, 21.0, 1.2, "Sickbeds / Recovery", "1 Hour", "High Biocompatibility & Gastrointestinal Rest"),
        IndianFoodEntry("Curd Rice", 125.0, 3.2, 19.0, 3.5, "Probiotic Rice", "1.5 Hours", "Cooling Gut Flora Support"),
        
        // INTEGRAL VEG CURRIES & DALS
        IndianFoodEntry("Dal Tadka", 95.0, 5.5, 13.0, 2.8, "Lentils", "1.5 Hours", "Plant Protein Clean Recovery"),
        IndianFoodEntry("Dal Makhani", 160.0, 6.2, 16.0, 8.5, "Rich Lentils", "3 Hours", "Slow Recovery Creamy Grain"),
        IndianFoodEntry("Sambar", 75.0, 2.8, 11.0, 1.8, "Lentil Soup", "1.5 Hours", "Fiber and Tangy Mineral Hydration"),
        IndianFoodEntry("Rasam", 45.0, 1.2, 8.0, 0.5, "Digestive Soup", "45 Mins", "Rapid Digestive Enzyme Trigger & Vitamin C"),
        IndianFoodEntry("Chana Masala", 140.0, 6.5, 22.0, 3.2, "Legumes Curry", "2.5 Hours", "Slow Glycemic Complex Grain"),
        IndianFoodEntry("Rajma Curry", 128.0, 6.0, 21.0, 2.4, "Legumes Curry", "2.5 Hours", "Kidney Bean Heart Friendly Starch"),
        IndianFoodEntry("Kadhi Pakora", 115.0, 4.0, 13.0, 5.5, "Gram Flour Gravy", "2 Hours", "Fermented Yogurt Acidic Fuel"),
        IndianFoodEntry("Paneer Butter Masala", 195.0, 9.5, 5.5, 15.5, "Paneer Curry", "3 Hours", "Lipophilic Nitrogen Support Casein"),
        IndianFoodEntry("Palak Paneer", 140.0, 8.2, 4.2, 10.5, "Paneer Curry", "2.5 Hours", "Iron & Vitamin A Rich Muscle Synthesis"),
        IndianFoodEntry("Kadai Paneer", 175.0, 9.0, 5.2, 13.5, "Paneer Curry", "3 Hours", "Spiced Anabolic Casein"),
        IndianFoodEntry("Shahi Paneer", 190.0, 8.8, 6.5, 15.0, "Paneer Curry", "3 Hours", "Rich Dairy Protein Blend"),
        IndianFoodEntry("Matar Paneer", 150.0, 8.4, 8.5, 9.8, "Paneer Curry", "2.5 Hours", "Pea Fiber & Paneer Mix"),
        IndianFoodEntry("Aloo Gobi", 90.0, 2.0, 11.0, 4.5, "Vegetable Curry", "2 Hours", "Potassium and Brassica Antioxidants"),
        IndianFoodEntry("Bhindi Masala", 85.0, 1.8, 9.0, 4.8, "Vegetable Curry", "1.5 Hours", "Slimy Soluble Mucilage Fiber Support"),
        IndianFoodEntry("Baingan Bharta", 80.0, 1.5, 8.0, 4.7, "Vegetable Curry", "1.5 Hours", "Smoked Low Calorie Fiber"),
        IndianFoodEntry("Mix Veg Makhani", 125.0, 2.8, 11.0, 8.0, "Rich Curry", "2.5 Hours", "Fat Soluble Vitamin Uptake Base"),
        
        // CHICKEN / MEAT / FISH / EGGS
        IndianFoodEntry("Chicken Curry", 155.0, 18.0, 2.5, 8.0, "Non-Veg", "2.5 Hours", "High Muscle Repair Leucine"),
        IndianFoodEntry("Butter Chicken", 230.0, 16.5, 4.5, 16.5, "Non-Veg Rich", "3 Hours", "High-Protein Rich Lipid Repair"),
        IndianFoodEntry("Chicken Tikka", 185.0, 24.0, 1.5, 9.2, "Non-Veg Tandoor", "2.5 Hours", "Ultra Lean High Recovery Proteins"),
        IndianFoodEntry("Tandoori Chicken", 175.0, 22.0, 1.0, 9.0, "Non-Veg Tandoor", "2.5 Hours", "Smoked Lean Gym Repair Amino Acids"),
        IndianFoodEntry("Fish Curry", 120.0, 15.0, 2.0, 5.8, "Non-Veg Sea", "2 Hours", "Omega-3 Anti-inflammatory Synthesis"),
        IndianFoodEntry("Fish Fry Spice", 210.0, 17.5, 5.0, 13.0, "Non-Veg Sea", "2.5 Hours", "Crunchy Omega Complex Support"),
        IndianFoodEntry("Egg Bhurji", 180.0, 11.5, 2.5, 14.0, "Eggs Spicy", "1.5 Hours", "Bio-optimized Egg Albumen & Lipids"),
        IndianFoodEntry("Egg Curry", 145.0, 10.0, 3.5, 10.0, "Eggs Curry", "2 Hours", "High Egg White Digestibility"),
        IndianFoodEntry("Boiled Egg", 155.0, 13.0, 1.1, 11.0, "Eggs Raw", "1.5 Hours", "High-efficiency Whole Reference Protein"),
        IndianFoodEntry("Omelette Plain", 160.0, 11.0, 1.2, 12.0, "Eggs Pan", "1.5 Hours", "Quick Frying Morning Recovery"),
        IndianFoodEntry("Mutton Curry", 240.0, 19.0, 3.0, 16.8, "Meat Heavy", "3.5 Hours", "Iron & Carnitine Loaded Red Fiber"),
        IndianFoodEntry("Prawn Masala", 130.0, 18.5, 2.0, 5.2, "Sea Shell", "2 Hours", "Zinc-heavy Mineral Replenishment"),
        
        // DAIRY & ESSENTIAL BASELINE
        IndianFoodEntry("Curd Plain", 63.0, 3.2, 4.1, 3.6, "Dairy Base", "1 Hour", "Lactobacillus Acidophilus Probiotic"),
        IndianFoodEntry("Yogurt Plain", 70.0, 3.5, 4.5, 3.8, "Dairy Base", "1 Hour", "Colon Flora Seeder Base"),
        IndianFoodEntry("Buttermilk", 30.0, 1.2, 2.5, 0.8, "Coolant Drinks", "45 Mins", "Body Heat Regulation & Electrolyte Restoration"),
        IndianFoodEntry("Paneer Raw", 265.0, 18.0, 2.5, 20.0, "Dairy Slab", "3 Hours", "Anabolic Casein Heavy Muscle Guard"),
        IndianFoodEntry("Lassi Plain Shaked", 90.0, 2.5, 14.0, 2.6, "Dairy Drink", "1.5 Hours", "Refreshing Sweet Probiotic Fuel"),
        IndianFoodEntry("Cow Ghee", 900.0, 0.0, 0.0, 100.0, "Noble Fats", "2 Hours", "Short Chain Butyric Acid Immunity Base"),
        IndianFoodEntry("Amul Butter", 717.0, 0.8, 0.0, 81.0, "Noble Fats", "2 Hours", "Heavy Saturated Vitamin Carrier"),
        IndianFoodEntry("Milk Cow Glass", 60.0, 3.2, 4.8, 3.2, "Liquid Feed", "1 Hour", "Sleep Inducing Tryptophan Complex"),
        
        // MINERAL RECOVERY / REFRESHERS
        IndianFoodEntry("Coconut Water", 19.0, 0.2, 4.2, 0.0, "Organic Electrolyte", "30 Mins", "Ultra Potassium Hyper-Hydrator"),
        IndianFoodEntry("Sugarcane Juice", 80.0, 0.1, 20.0, 0.1, "Rapid Energizer", "30 Mins", "Direct Liver Glycogen Recharger"),
        IndianFoodEntry("Lemonade", 25.0, 0.1, 6.2, 0.0, "Hydrator", "30 Mins", "pH Balancing Citric Replenisher"),
        IndianFoodEntry("Jal Jeera", 15.0, 0.2, 3.5, 0.0, "Digestive Drinks", "30 Mins", "Salt Retaining & Anti-Flatulant Blend"),
        IndianFoodEntry("Masala Chai", 45.0, 1.2, 6.0, 1.5, "Adaptogen Drinks", "45 Mins", "Ginger & Cardamom Neural Refreshment"),
        IndianFoodEntry("Filter Coffee", 50.0, 1.5, 6.1, 1.6, "Arousal Drinks", "45 Mins", "Polyphenol & Caffeine Stimulation"),
        IndianFoodEntry("Badam Milk", 110.0, 3.5, 14.0, 4.5, "Power Drinks", "1.5 Hours", "Manganese & Almond Lipid Base"),
        
        // SNACKS & CHAATS
        IndianFoodEntry("Samosa", 310.0, 4.5, 38.0, 15.5, "Savory Snack", "3 Hours", "Spiced Fried Wheat Envelope"),
        IndianFoodEntry("Kachori", 380.0, 6.5, 44.0, 19.5, "Savory Snack", "3 Hours", "Heavy Lentil Starch Dough"),
        IndianFoodEntry("Onion Pakora", 315.0, 5.0, 32.0, 18.0, "Savory Snack", "2.5 Hours", "Deep Fried Gram Flour Onion"),
        IndianFoodEntry("Bhel Puri", 185.0, 4.0, 35.0, 3.1, "Street Food Chati", "1.5 Hours", "Puffed Rice Light Morning Treat"),
        IndianFoodEntry("Sev Puri", 250.0, 4.2, 39.0, 8.5, "Street Food Chati", "1.5 Hours", "Spiced Vegetable Crisps"),
        IndianFoodEntry("Pani Puri", 150.0, 2.5, 29.0, 2.5, "Street Food Chati", "1 Hour", "Spiced Mint Hydroplex Shot"),
        IndianFoodEntry("Dahi Vada", 180.0, 6.0, 24.0, 6.5, "Street Food Chati", "2 Hours", "Probiotic Creamy Lentil Treat"),
        IndianFoodEntry("Pav Bhaji", 220.0, 4.8, 32.0, 8.0, "Starchy Meal", "2.5 Hours", "Spiced Butter Loaded Vegetable Puree"),
        IndianFoodEntry("Misal Pav", 290.0, 9.0, 38.0, 11.2, "Sprout Energy", "2.5 Hours", "Peptide-Loaded Spicy Legume Sprout Soup"),
        IndianFoodEntry("Sabudana Vada", 360.0, 2.0, 54.0, 15.0, "Fried Savory", "2.5 Hours", "Tapioca Deep Fried Fuel"),
        IndianFoodEntry("Popcorn Plain", 385.0, 12.0, 74.0, 4.5, "Snacks High Fiber", "1 Hour", "Cellulose Shell Insoluble Bulking"),
        IndianFoodEntry("Makhana Roasted", 350.0, 9.7, 76.0, 0.1, "Superfood Snack", "1.5 Hours", "Alkaline L-Arginine Anti-Aging Puffs"),
        IndianFoodEntry("Roasted Chana", 370.0, 22.0, 56.0, 5.5, "Superfood Snack", "2 Hours", "Ultra Bio-Available Plant Protein & Fibre"),
        IndianFoodEntry("Peanuts Masala", 567.0, 25.0, 16.0, 49.0, "Lipid Seeds", "2.5 Hours", "High Arginine & CoQ10 Energy Packs"),
        
        // INDIAN SWEETS / TREATS
        IndianFoodEntry("Rasgulla", 186.0, 4.0, 41.5, 1.0, "Chhena Sweets", "1 Hour", "Squeezed Dairy Sponge Quick Glucose"),
        IndianFoodEntry("Gulab Jamun", 380.0, 5.0, 59.0, 14.0, "Heavy Sweets", "2.5 Hours", "High Glycemic Fat-Sugary Carb Explosion"),
        IndianFoodEntry("Kheer Plain", 150.0, 3.8, 24.0, 4.2, "Dairy Sweet", "1.5 Hours", "Rice Milk Sleep Supporter"),
        IndianFoodEntry("Shrikhand", 280.0, 6.0, 52.0, 5.5, "Dairy Sweet", "2 Hours", "Concentrated Fermented Whey Carb"),
        IndianFoodEntry("Apple Fruit", 52.0, 0.3, 14.0, 0.2, "Fruits High Fiber", "1 Hour", "Metabolic Antioxidants (Pectin)"),
        IndianFoodEntry("Banana Yellow", 89.0, 1.1, 23.0, 0.3, "Fruits Fast Active", "45 Mins", "Muscle Cramp Repellent Potassium")
    )

    fun searchLocalFood(query: String): IndianFoodEntry? {
        val q = query.trim().lowercase(Locale.ROOT)
        if (q.isEmpty()) return null
        
        // 1. Double check exact matching
        foods.firstOrNull { it.name.lowercase(Locale.ROOT) == q }?.let { return it }
        
        // 2. Contains matching (preferred)
        foods.firstOrNull { q.contains(it.name.lowercase(Locale.ROOT)) || it.name.lowercase(Locale.ROOT).contains(q) }?.let { return it }
        
        // 3. Fallback to basic keyword tokens if query contains spaces (e.g. "plate of idli" matching "idli")
        val tokens = q.split("\\s+".toRegex())
        for (token in tokens) {
            if (token.length > 2) {
                foods.firstOrNull { it.name.lowercase(Locale.ROOT).contains(token) }?.let { return it }
            }
        }
        return null
    }
}
