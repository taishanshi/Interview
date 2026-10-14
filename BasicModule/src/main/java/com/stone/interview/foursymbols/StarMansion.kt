package com.stone.interview.foursymbols

data class StarMansion(
    val name: String,
    val animal: String,
    val meaning: String,
)

object FourSymbolsData {
    val qingLong = listOf(
        StarMansion("角", "蛟", "东方苍龙之角，开春号令，主生发"),
        StarMansion("亢", "龙", "龙颈，主礼法与疾疫"),
        StarMansion("氐", "貉", "龙足，主根本、路途"),
        StarMansion("房", "兔", "龙腹，明堂之宿，主车驾"),
        StarMansion("心", "狐", "龙心，大火之次，主赏罚"),
        StarMansion("尾", "虎", "龙尾，主后宫、风水"),
        StarMansion("箕", "豹", "龙尾末端，主口舌、风"),
    )

    val baiHu = listOf(
        StarMansion("奎", "狼", "白虎前足，主沟渎、文章"),
        StarMansion("娄", "狗", "白虎身，主苑牧、牺牲"),
        StarMansion("胃", "雉", "仓廪之宿，主收藏"),
        StarMansion("昴", "鸡", "胡星，主狱事、兵戈"),
        StarMansion("毕", "乌", "边兵之象，主弋猎"),
        StarMansion("觜", "猴", "白虎头，主葆旅"),
        StarMansion("参", "猿", "白虎后身，主杀伐"),
    )

    val zhuQue = listOf(
        StarMansion("井", "犴", "朱雀头，主水泉、法令"),
        StarMansion("鬼", "羊", "视之宿，主祭祀、死丧"),
        StarMansion("柳", "獐", "朱雀喙，主草木、宴会"),
        StarMansion("星", "马", "朱雀目，主急变"),
        StarMansion("张", "鹿", "朱雀嗉，主珍宝、赏赉"),
        StarMansion("翼", "蛇", "朱雀翼，主礼乐、远客"),
        StarMansion("轸", "蚓", "朱雀尾，主车骑、风"),
    )

    val xuanWu = listOf(
        StarMansion("斗", "獬", "玄武头，天庙，主爵禄"),
        StarMansion("牛", "牛", "牵牛，主牺牲、关梁"),
        StarMansion("女", "蝠", "须女，主布帛、嫁娶"),
        StarMansion("虚", "鼠", "玄武颈，主丧哭、危难"),
        StarMansion("危", "燕", "屋顶之象，主架屋"),
        StarMansion("室", "猪", "营室，主宫室、土功"),
        StarMansion("壁", "貐", "东壁，主文章、图书"),
    )
}
