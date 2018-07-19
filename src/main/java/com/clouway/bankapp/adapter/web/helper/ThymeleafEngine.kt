package com.clouway.bankapp.adapter.web.helper

import spark.template.thymeleaf.ThymeleafTemplateEngine

class ThymeleafEngine {

    private val thymeleafTemplateEngine by lazy {
        ThymeleafTemplateEngine()
    }

    fun getEngine(): ThymeleafTemplateEngine {
        return thymeleafTemplateEngine
    }

}