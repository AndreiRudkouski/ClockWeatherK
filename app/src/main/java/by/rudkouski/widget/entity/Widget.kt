package by.rudkouski.widget.entity

import androidx.room.*

@Entity(tableName = "widgets",
    foreignKeys = [(ForeignKey(entity = Location::class, parentColumns = ["location_id"], childColumns = ["widget_location_id"]))],
    indices = [Index(value = ["widget_location_id"])])
data class Widget(@PrimaryKey
                  @ColumnInfo(name = "widget_id")
                  val id: Int,
                  @ColumnInfo(name = "widget_bold")
                  val isBold: Boolean,
                  @ColumnInfo(name = "widget_theme")
                  val themeId: Int,
                  @ColumnInfo(name = "widget_location_id")
                  var locationId: Int)