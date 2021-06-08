package com.mobatia.nasmanila.fragments.absences.model

import java.io.Serializable

class LeavesModel: Serializable {
    var id: String? = null
    var fromDate: String? = null
    var toDate: String? = null
    var reason: String? = null
    var status: String? = null
    var createdTime: String? = null
}