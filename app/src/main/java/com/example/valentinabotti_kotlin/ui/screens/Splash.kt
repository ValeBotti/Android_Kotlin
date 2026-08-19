import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.valentinabotti_kotlin.model.Screen
import kotlinx.coroutines.delay

@Composable
fun Splash(modifier: Modifier = Modifier, navController: NavController) {

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center // Centra il contenuto
    ) {
        CircularProgressIndicator(
            color = Color.LightGray, // Colore grigio chiaro
            strokeWidth = 4.dp      // Spessore dell'indicatore
        )
    }

    LaunchedEffect(Unit) {
        navController.navigate(Screen.HomeListaMenu.route) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }
}