package com.example.driedmeatsrecipes.ui.navigation

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
//import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.driedmeatsrecipes.data.Recipe
import com.example.driedmeatsrecipes.ui.screens.AddEditRecipeScreen
import com.example.driedmeatsrecipes.ui.screens.RecipeDetailScreen
import com.example.driedmeatsrecipes.ui.screens.RecipeGridScreen
import com.example.driedmeatsrecipes.ui.viewmodels.RecipeViewModel

sealed class Screen(val route: String) {
    object RecipeGrid : Screen("recipeGrid")
    object AddRecipe : Screen("addRecipe")
    object EditRecipe : Screen("editRecipe/{recipeId}") {
        fun createRoute(recipeId: Int) = "editRecipe/$recipeId"
    }
    object RecipeDetail : Screen("recipeDetail/{recipeId}") {
        fun createRoute(recipeId: Int) = "recipeDetail/$recipeId"
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: RecipeViewModel = hiltViewModel()
    val recipes by viewModel.recipes.collectAsState(initial = emptyList())

    var showDeleteDialog by remember { mutableStateOf<Recipe?>(null) }

    // Диалог за изтриване
    showDeleteDialog?.let { recipeToDelete ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Изтриване на рецепта") },
            text = { Text("Сигурни ли сте, че искате да изтриете \"${recipeToDelete.title}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteRecipe(recipeToDelete)
                        showDeleteDialog = null
                        navController.popBackStack()
                    }
                ) {
                    Text("Изтрий")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Отмени")
                }
            }
        )
    }

    NavHost(
        navController = navController,
        startDestination = Screen.RecipeGrid.route
    ) {
        composable(Screen.RecipeGrid.route) {
            RecipeGridScreen(
                navController = navController,
                recipes = recipes,
                onAddRecipe = {
                    navController.navigate(Screen.AddRecipe.route)
                },
                onRecipeClick = { recipeId ->
                    navController.navigate(Screen.RecipeDetail.createRoute(recipeId))
                }
            )
        }

        composable(Screen.AddRecipe.route) {
            AddEditRecipeScreen(
                navController = navController,
                recipe = null,
                onSaveRecipe = { recipe ->
                    viewModel.insertRecipe(recipe)
                }
            )
        }

        composable(
            route = Screen.EditRecipe.route,
            arguments = listOf(navArgument("recipeId") { type = NavType.IntType })
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getInt("recipeId") ?: 0
            val recipe = remember(recipeId) {
                recipes.find { it.id == recipeId }
            }

            AddEditRecipeScreen(
                navController = navController,
                recipe = recipe,
                onSaveRecipe = { updatedRecipe ->
                    viewModel.updateRecipe(updatedRecipe)
                }
            )
        }

        composable(
            route = Screen.RecipeDetail.route,
            arguments = listOf(navArgument("recipeId") { type = NavType.IntType })
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getInt("recipeId") ?: 0
            val recipe = remember(recipeId) {
                recipes.find { it.id == recipeId }
            }

            RecipeDetailScreen(
                navController = navController,
                recipe = recipe,
                onEditClick = { id ->
                    navController.navigate(Screen.EditRecipe.createRoute(id))
                },
                onDeleteClick = { recipeToDelete ->
                    showDeleteDialog = recipeToDelete
                }
            )
        }
    }
}

