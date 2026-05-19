import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

abstract class PrintString: DefaultTask(){
    @get:Input
    abstract val str: Property<String>

    @TaskAction
    fun action(){
        println(str.get())
    }
}