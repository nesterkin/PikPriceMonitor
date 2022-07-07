import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import java.io.BufferedWriter
import java.io.File
import java.io.File.separatorChar
import java.io.FileWriter
import java.nio.file.Paths
import java.util.*


object Main {
    private const val baseUrl =
        "https://api.pik.ru/v2/flat?block_id=118&bulk_id=&layout_id=&price_from=&price_to=&floor_from=&floor_to=&area_from=&area_to=&rooms=&studio=&penthouse=&settlement=&settlement_from=&settlement_to=&isFurniture=&finish=&settlement_fact=&initial_payment=&monthly_payment=&order=price&price_million=0&index_by=statistics&images=1&metadata=1&layouts=1&type=1,2&page="

    private lateinit var client: OkHttpClient
    private val date = Calendar.getInstance().time.toString()
    private val responseDir = Paths.get("").toAbsolutePath().toString() + separatorChar +
            "src" + separatorChar +
            "main" + separatorChar +
            "response" + separatorChar
    private val prettyFlats = responseDir + "PrettyAllFlats " + date + ".json"
    private val gson = GsonBuilder().setPrettyPrinting().create()

    @JvmStatic
    fun main(args: Array<String>) {
        makeHttp()
        val flats = mutableListOf<Flat>()
        var i = 1

        File(responseDir).mkdirs()

        do {
            val url = baseUrl + i
            val pikResponse = gson.fromJson(makeResponse(url), PikResponse::class.java)
            val prettyWriter = BufferedWriter(FileWriter(responseDir + "PrettyPage$i " + date + ".json"))
            prettyWriter.write(gson.toJson(pikResponse))
            prettyWriter.close()
            i++

            flats.addAll(pikResponse.flats)
        } while (pikResponse.flats.isNotEmpty())

        val prettyWriter = BufferedWriter(FileWriter(prettyFlats))
        val myCustomArray = gson.toJsonTree(flats).asJsonArray
        val jsonObject = JsonObject().apply {
            add("flats", myCustomArray)
        }
        prettyWriter.write(gson.toJson(jsonObject))
        prettyWriter.close()

        val outputFlats = mutableListOf<PrintableFlat>()
        flats
            .filter { it.status == "free" }
//            .filter { it.floor > 5 }
//            .filter { it.bulk.name != "Корпус 27" }
//            .filter { it.bulk.name != "Корпус 38" }
//            .filter { it.bulk.name != "Корпус 39" }
//            .filter { it.bulk.name != "Корпус 40" }
//            .filter { it.rooms == "2" }
//            .filter { it.bulk.settlement_year == 2021 }
            .forEach {
                outputFlats.add(
                    PrintableFlat(
                        (it.price / it.area).toInt(),
                        it.price,
                        it.area,
                        it.rooms_fact,
                        it.bulk.name,
                        it.bulk.settlement_year,
                        "https://www.pik.ru/sp/flats/" + it.id,
                        it.status
                    )
                )
            }

        val filteredOutputFlats = outputFlats.sortedBy { it.pricePerMeter }

        for ((index, flat) in filteredOutputFlats.withIndex()) {
            println("$index $flat")
        }
    }

    private fun makeHttp() {
        val logging = HttpLoggingInterceptor()
        logging.level = (HttpLoggingInterceptor.Level.BASIC)
        client = OkHttpClient.Builder().addInterceptor(logging).build()
    }

    private fun makeResponse(url: String): String? {
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).execute().use { response ->
            if (response.isSuccessful && response.body != null) {
                return response.body?.string()
            }
        }

        return null
    }
}