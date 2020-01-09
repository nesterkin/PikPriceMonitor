import java.util.*

data class PikResponse(
    val block: Block,
    val bulks: List<Bulk>,
    val rooms: List<Any>,
    val price: Price,
    val area: Area,
    val settlement: List<Int>,
    val count: Int,
    val flats: List<Flat>
)

data class Block(
    val id: Int,
    val name: String,
    val url: String,
    val path: String
)

data class Bulk(
    val id: Int,
    val building_status: Int,
    val type_id: Int,
    val name: String,
    val title: String,
    val settlement_year: Int,
    val min_price: Int,
    val max_price: Int,
    val min_area: Int,
    val max_area: Int,
    val is_furniture: Int
)

data class Price(
    val min: Int,
    val max: Int
)

data class Area(
    val min: Double,
    val max: Double
)

data class Flat(
    val id: Int,
    val guid: UUID,
    val status: String,
    val is_studio: Int,
    val is_penthouse: Int,
    val type_id: Int,
    val floor: Int,
    val rooms: String,
    val rooms_fact: Int,
    val number: Int,
    val price: Int,
    val discount: Int,
    val area: Double,
    val finish: Finish,
    val furniture: Boolean,
    val booking_cost: String,
    val block: Block,
    val bulk: Bulk,
    val section: Section,
    val layout: Layout
)

data class Finish(
    val isFinish: Boolean,
    val whiteBox: Boolean
)

data class Section(
    val id: Int,
    val number: Int
)

data class Layout(
    val id: Int,
    val flat_plan_png: String
)

data class FlatWrapper(val flats: List<Flat>)