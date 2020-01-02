package com.gaelmarhic.quadrant

import com.android.build.gradle.*
import com.android.build.gradle.api.AndroidSourceSet
import com.android.build.gradle.api.BaseVariant
import com.gaelmarhic.quadrant.QuadrantConstants.PLUGIN_NAME
import com.gaelmarhic.quadrant.QuadrantConstants.TARGET_DIRECTORY
import org.gradle.api.DomainObjectCollection
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.ExtensionContainer
import java.io.File
import kotlin.reflect.KClass

class QuadrantPlugin : Plugin<Project> {

    override fun apply(project: Project) = with(project) {
        plugins.all { plugin ->
            when (plugin) {
                is AppPlugin -> {
                    applyPlugin(AppExtension::class) { it.applicationVariants }
                }
                is LibraryPlugin -> {
                    applyPlugin(LibraryExtension::class) { it.libraryVariants }
                }
                else -> throw UnsupportedOperationException(WRONG_MODULE_TYPE_ERROR_MESSAGE)
            }
        }
    }

    private fun <E : BaseExtension, V : BaseVariant> Project.applyPlugin(
        extensionType: KClass<E>,
        block: (E) -> DomainObjectCollection<V>
    ) {
        val extension = getExtension(extensionType)
        val variants = block(extension)
        val mainSourceSet = extension.sourceSet(MAIN_SOURCE_SET)
        addTargetDirectoryToSourceSet(mainSourceSet)
    }

    private fun <V : BaseVariant> Project.registerTask(
        task: Task,
        variants: DomainObjectCollection<V>
    ) {
        // TODO: To be implemented.
    }

    private fun Project.addTargetDirectoryToSourceSet(sourceSet: AndroidSourceSet) {
        sourceSet.java.srcDir("$buildDir${File.separator}$TARGET_DIRECTORY")
    }

    private fun <E : BaseExtension> Project.getExtension(type: KClass<E>) = extensions[type]

    private operator fun <T : BaseExtension> ExtensionContainer.get(type: KClass<T>): T {
        return getByType(type.java)
    }

    private fun BaseExtension.sourceSet(name: String) = sourceSets.getByName(name)

    companion object {

        private const val MAIN_SOURCE_SET = "main"
        private const val WRONG_MODULE_TYPE_ERROR_MESSAGE =
            "$PLUGIN_NAME can only be applied to Android Application or Android Library modules."
    }
}
