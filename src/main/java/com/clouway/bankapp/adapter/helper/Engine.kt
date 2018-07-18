package com.clouway.bankapp.adapter.helper

import spark.template.thymeleaf.ThymeleafTemplateEngine

class Engine {

    companion object {

        private val thymeleafTemplateEngine by lazy {
            ThymeleafTemplateEngine()
        }

        fun getEngine(): ThymeleafTemplateEngine {
            return thymeleafTemplateEngine
        }

    }

}