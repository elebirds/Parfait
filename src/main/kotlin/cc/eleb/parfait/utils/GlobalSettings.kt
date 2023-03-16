package cc.eleb.parfait.utils

object GlobalSettings {
    var LANGUAGE = 0
    var SCORE_TYPE = 0
    var OUTPUT_STRING = ""

    fun loadFromPrefs() {
        LANGUAGE = ParfaitPrefs.state.getInt("gloSetting.language", 0)
        OUTPUT_STRING = ParfaitPrefs.state.get(
            "gloSetting.outputString",
            "姓名：%name，性别:%gender，学号:%id，系%school%grade级%prof专业的学生。截至目前，该生所修读的所有课程的加权平均分为%as。"
        )
        SCORE_TYPE = ParfaitPrefs.state.getInt("gloSetting.scoreType", 0)
    }
}