package ame_digital.challenge_back

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import jakarta.websocket.server.PathParam

@SpringBootApplication class ChallengeBackendApplication

fun main(args: Array<String>) {
        runApplication<ChallengeBackendApplication>(*args)
}

data class CriarPlanetaRequest(val nome: String?, val clima: String?, val terreno: String?)

data class Response(val message: String, val status: Int)

object Constants{
        const val NO_ROWS = 0,
        const val MINIMUM_VALID_ID = 1
}

@RestController
class PlanetaController(val db: JdbcTemplate) {
        @PostMapping("/planetas")
        fun inserir(@RequestBody request: CriarPlanetaRequest): ResponseEntity<Response> {
                if (request.nome.isNullOrEmpty()) {
                        val responseBody =
                                        Response(
                                                        message = "campo nome é obrigatório", 
                                                        status = 400
                                        )

                        return ResponseEntity(responseBody, HttpStatus.BAD_REQUEST)
                }

                if (request.clima.isNullOrEmpty()) {
                        val responseBody =
                                        Response(
                                                        message = "campo clima é obrigatório",
                                                        status = 400
                                        )

                        return ResponseEntity(responseBody, HttpStatus.BAD_REQUEST)
                }

                if (request.terreno.isNullOrEmpty()) {
                        val responseBody =
                                        Response(
                                                        message = "campo terreno é obrigatório",
                                                        status = 400
 
                                                )
                        return ResponseEntity(responseBody, HttpStatus.BAD_REQUEST)
                }

                try {
                        db.update(
                                        "insert into planetas values(?,?,?)",
                                        request.nome,
                                        request.clima,
                                        request.terreno
                        )
                } catch(e) {
                        // TODO: Error Handleing 
                }

                val res = Response(message = "criado com sucesso", status = 200)
                return ResponseEntity(res, HttpStatus.OK)
        }

        @DeleteMapping("/planetas/{id}")
        fun deletar(@PathVariable id: Int): ResponseEntity<Response> {
                if(id < Constants.MINIMUM_VALID_ID) {
                        return ResponseEntity(
                                Response(message = "id invalido", status = 400),
                                HttpStatus.BAD_REQUEST
                        )
                }

                val isPlanetaExistente = db.queryForObject<Int>(
                        "SELECT COUNT(*) FROM PLANETAS WHERE id = ?", id)!!

                if (isPlanetaExistente == Constants.NO_ROWS) {
                        return ResponseEntity(
                                Response(message = "planeta nao existe", status = 404),
                                HttpStatus.BAD_REQUEST
                        )
                }

                db.update("DELETE FROM planetas WHERE id = ?", id)

                return ResponseEntity(Response(message = "", status = 204), HttpStatus.NO_CONTENT)
    }
}
