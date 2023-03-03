package cc.eleb.parfait

import io.vproxy.vfx.control.globalscreen.GlobalScreenUtils
import io.vproxy.vfx.manager.task.TaskManager
import io.vproxy.vfx.ui.scene.VScene
import io.vproxy.vfx.ui.stage.VStage
import javafx.application.Application
import javafx.stage.Stage

class HelloApplication : Application() {
    private val mainScenes = ArrayList<VScene>()
    override fun start(stage: Stage) {
        val vs = object : VStage(stage) {
            override fun close() {
                super.close()
                TaskManager.get().terminate()
                GlobalScreenUtils.unregister()
            }
        }
        vs.initialScene.enableAutoContentWidthHeight()
        vs.setTitle("Parfait")
        vs.useLightBorder()

        vs.stage.width = 1280.0
        vs.stage.height = 800.0
        vs.stage.centerOnScreen()
        vs.stage.show()
    }
}

fun main() {
    Application.launch(HelloApplication::class.java)
}