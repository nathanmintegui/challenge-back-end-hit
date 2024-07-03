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
import org.springframework.web.bind.annotation.GetMapping
import jakarta.websocket.server.PathParam

@SpringBootApplication class ChallengeBackendApplication

fun main(args: Array<String>) {
        runApplication<ChallengeBackendApplication>(*args)
}

class Planeta(
        val id: Int,
        val nome: String,
        val clima: String
        val terreno: String
)

data class CriarPlanetaRequest(val nome: String?, val clima: String?, val terreno: String?)

data class Response(val message: String, val body: kotlin.Any?, val status: Int)

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
                                                        body = null,
                                                        status = 400
                                        )

                        return ResponseEntity(responseBody, HttpStatus.BAD_REQUEST)
                }

                if (request.clima.isNullOrEmpty()) {
                        val responseBody =
                                        Response(
                                                        message = "campo clima é obrigatório",
                                                        body = null,
                                                        status = 400
                                        )

                        return ResponseEntity(responseBody, HttpStatus.BAD_REQUEST)
                }

                if (request.terreno.isNullOrEmpty()) {
                        val responseBody =
                                        Response(
                                                        message = "campo terreno é obrigatório",
                                                        body = null,
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

                val res = Response(message = "criado com sucesso", body = null, status = 200)
                return ResponseEntity(res, HttpStatus.OK)
        }

        @DeleteMapping("/planetas/{id}")
        fun deletar(@PathVariable id: Int): ResponseEntity<Response> {
                if(id < Constants.MINIMUM_VALID_ID) {
                        return ResponseEntity(
                                Response(message = "id invalido", body = null, status = 400),
                                HttpStatus.BAD_REQUEST
                        )
                }

                val isPlanetaExistente = db.queryForObject<Int>(
                        "SELECT COUNT(*) FROM PLANETAS WHERE id = ?", id)!!

                if (isPlanetaExistente == Constants.NO_ROWS) {
                        return ResponseEntity(
                                Response(message = "planeta nao existe", body = null, status = 404),
                                HttpStatus.BAD_REQUEST
                        )
                }

                db.update("DELETE FROM planetas WHERE id = ?", id)

                return ResponseEntity(
                        Response(message = "", body = null, status = 204),
                        HttpStatus.NO_CONTENT
                )
    }

        @GetMapping("/planetas/{id}")
        fun listarPorId(@PathVariable id: Int): ResponseEntity<Response> {
                if(id < Constants.MINIMUM_VALID_ID) {
                        return ResponseEntity(
                                Response(message = "id invalido", body = null, status = 400),
                                HttpStatus.BAD_REQUEST
                        )
                }

                var planeta = db.queryForObject(
                "SELECT * FROM planetas WHERE id = ?",
                id) {p , _ -> 
                        Planeta(
                                p.getInt("id"),
                                p.getString("nome"),
                                p.getString("clima"),
                                p.getString("terreno")
                        )
                }

                if(planeta is null) {
                        return ResponseEntity(
                                Response(message = "planeta nao existe", body = null, status = 404),
                                HttpStatus.BAD_REQUEST
                        )
                }

                return ResponseEntity(
                        Response(message = "", body = planeta, status = 204),
                        HttpStatus.NO_CONTENT
                )
        }
}
