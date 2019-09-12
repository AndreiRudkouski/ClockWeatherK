package by.rudkouski.widget.entity

import android.content.Context
import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import by.rudkouski.widget.database.converter.SettingCodeConverter
import by.rudkouski.widget.database.converter.SettingTypeConverter
import java.util.Locale.getDefault

@Entity(tableName = "settings",
    foreignKeys = [(ForeignKey(onDelete = CASCADE, entity = Widget::class, parentColumns = ["widget_id"], childColumns = ["setting_widget_id"]))],
    indices = [Index(value = ["setting_widget_id"])])
@TypeConverters(SettingTypeConverter::class, SettingCodeConverter::class)
data class Setting(@ColumnInfo(name = "setting_name_code")
                   val code: Code,
                   @ColumnInfo(name = "setting_value")
                   val value: Int,
                   @ColumnInfo(name = "setting_type")
                   val type: Type,
                   @ColumnInfo(name = "setting_widget_id")
                   var widgetId: Int?) {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "setting_id")
    var id: Int = 0

    fun getName(context: Context): String {
        return context.getString(context.resources.getIdentifier(code.toString().toLowerCase(getDefault()), "string", context.packageName))
    }

    fun getDescription(context: Context): String {
        return context.getString(
            context.resources.getIdentifier(code.toString().toLowerCase(getDefault()) + "_description", "string", context.packageName))
    }

    fun getBooleanValue(): Boolean {
        return if (type == Type.BOOLEAN) value != 0 else false
    }

    enum class Code {
        SETTING_BOLD,
        SETTING_THEME
    }

    enum class Type {
        BOOLEAN,
        NUMBER
    }
}