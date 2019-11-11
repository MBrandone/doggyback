package doggy.back.quizz

import doggy.back.ApiError
import doggy.back.defi.BrandoneEstUnConException
import doggy.back.defi.Defi
import doggy.back.defi.ProcessResponseUseCase
import doggy.back.parties.BenoitEstUnConException
import doggy.back.parties.PartiesRepository
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class QuizzController(
    private val partiesRepository: PartiesRepository,
    private val useCase: ProcessResponseUseCase
) {

    @PostMapping("/parties")
    fun postPartie(@RequestBody joueur: Joueur): Partie {
        val partie = Partie.init(joueur.joueur)
        partiesRepository.creerPartie(partie)
        return partie
    }

    @PostMapping("/parties/{partieId}/reponse")
    fun repondreAuDefi(
        @PathVariable("partieId") idPartie: String,
        @RequestBody reponse: Reponse
    ): Defi {
        return useCase.execute(idPartie, reponse)
    }
}

@ControllerAdvice(assignableTypes = [QuizzController::class])
class QuizzControllerAdvice {

    @ExceptionHandler(BenoitEstUnConException::class)
    fun handleFuck(e: BenoitEstUnConException): ResponseEntity<ApiError>? {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .contentType(MediaType.APPLICATION_JSON)
            .body(ApiError("Tema wesh, y a pas de partie avec l'id ${e.id} !"))
    }

    @ExceptionHandler(BrandoneEstUnConException::class)
    fun handleFuck2(e: BrandoneEstUnConException): ResponseEntity<ApiError>? {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .body(ApiError("Tema wesh, la partie avec l'id ${e.id}, elle est finie !"))
    }
}


data class Correction(
    val idDefi: String,
    val estCorrecte: Boolean
)


data class Reponse(
    val idDefi: String,
    val texte: String
)

data class Joueur(
    val joueur: String
)

data class Partie(
    val id: String,
    val joueur: String,
    val score: Int,
    val statut: String
) {
    companion object {
        fun init(joueur: String): Partie {
            return Partie(
                UUID.randomUUID().toString(),
                joueur,
                0,
                PartieStatut.EN_COURS.toString()
            )
        }
    }
}

enum class PartieStatut {
    EN_COURS,
    FINIE
}