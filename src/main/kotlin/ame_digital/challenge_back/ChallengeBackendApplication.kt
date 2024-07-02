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

@SpringBootApplication class ChallengeBackendApplication

fun main(args: Array<String>) {
        runApplication<ChallengeBackendApplication>(*args)
}

data class CriarPlanetaRequest(val nome: String?, val clima: String?, val terreno: String?)

data class Response(val message: String, val status: Int)

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
}
