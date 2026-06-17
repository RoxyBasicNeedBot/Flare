package com.roxy.flare.compose

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import com.roxy.flare.FlareAnimationType
import com.roxy.flare.FlareInternalApi
import com.roxy.flare.FlarePosition

@FlareInternalApi
object FlareComposeAnimations {

    fun getEnterTransition(
        position: FlarePosition,
        animationType: FlareAnimationType
    ): EnterTransition {
        return when (animationType) {
            FlareAnimationType.SLIDE -> {
                when (position) {
                    FlarePosition.TOP -> slideInVertically(
                        initialOffsetY = { -it },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    ) + fadeIn()
                    FlarePosition.BOTTOM -> slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    ) + fadeIn()
                    FlarePosition.CENTER -> scaleIn(
                        initialScale = 0.8f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    ) + fadeIn()
                }
            }
            FlareAnimationType.FADE -> fadeIn(
                animationSpec = spring(
                    stiffness = Spring.StiffnessLow
                )
            )
            FlareAnimationType.BOUNCE -> {
                when (position) {
                    FlarePosition.TOP -> slideInVertically(
                        initialOffsetY = { -it },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    ) + fadeIn()
                    FlarePosition.BOTTOM -> slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    ) + fadeIn()
                    FlarePosition.CENTER -> scaleIn(
                        initialScale = 0.7f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    ) + fadeIn()
                }
            }
        }
    }

    fun getExitTransition(
        position: FlarePosition,
        animationType: FlareAnimationType
    ): ExitTransition {
        return when (animationType) {
            FlareAnimationType.SLIDE -> {
                when (position) {
                    FlarePosition.TOP -> slideOutVertically(
                        targetOffsetY = { -it },
                        animationSpec = spring(stiffness = Spring.StiffnessMedium)
                    ) + fadeOut()
                    FlarePosition.BOTTOM -> slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = spring(stiffness = Spring.StiffnessMedium)
                    ) + fadeOut()
                    FlarePosition.CENTER -> scaleOut(
                        targetScale = 0.8f,
                        animationSpec = spring(stiffness = Spring.StiffnessMedium)
                    ) + fadeOut()
                }
            }
            FlareAnimationType.FADE -> fadeOut(
                animationSpec = spring(stiffness = Spring.StiffnessMedium)
            )
            FlareAnimationType.BOUNCE -> {
                when (position) {
                    FlarePosition.TOP -> slideOutVertically(
                        targetOffsetY = { -it - 100 },
                        animationSpec = spring(stiffness = Spring.StiffnessHigh)
                    ) + fadeOut()
                    FlarePosition.BOTTOM -> slideOutVertically(
                        targetOffsetY = { it + 100 },
                        animationSpec = spring(stiffness = Spring.StiffnessHigh)
                    ) + fadeOut()
                    FlarePosition.CENTER -> scaleOut(
                        targetScale = 0.6f,
                        animationSpec = spring(stiffness = Spring.StiffnessHigh)
                    ) + fadeOut()
                }
            }
        }
    }
}
