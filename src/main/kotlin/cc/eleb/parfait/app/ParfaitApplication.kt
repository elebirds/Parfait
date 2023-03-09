package cc.eleb.parfait.app

import io.vproxy.vfx.control.globalscreen.GlobalScreenUtils
import io.vproxy.vfx.manager.task.TaskManager
import io.vproxy.vfx.ui.scene.VScene
import io.vproxy.vfx.ui.stage.VStage

import javafx.application.Application
import javafx.stage.Stage

class ParfaitApplication : Application() {
    private val mainScenes = ArrayList<VScene>()
    override fun start(stage: Stage) {
        gs = object : VStage(stage) {
            override fun close() {
                super.close()
                TaskManager.get().terminate()
                GlobalScreenUtils.unregister()
            }
        }
        gs.initialScene.enableAutoContentWidthHeight()
        gs.setTitle("Parfait")
        gs.useLightBorder()

        gs.stage.width = 1280.0
        gs.stage.height = 800.0
        gs.stage.centerOnScreen()
        gs.stage.show()
    }

    companion object {
        @JvmStatic
        lateinit var gs: VStage
    }
}