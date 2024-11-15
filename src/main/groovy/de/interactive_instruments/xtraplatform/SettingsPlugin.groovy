package de.interactive_instruments.xtraplatform

import org.gradle.api.Plugin
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ModuleVersionIdentifier
import org.gradle.api.initialization.Settings

/**
 * @author zahnen
 */
class SettingsPlugin implements Plugin<Settings> {

    @Override
    void apply(Settings settings) {

        settings.plugins.apply('org.danilopianini.gradle-pre-commit-git-hooks')

        def version = getVersion(settings)

        settings.gradle.beforeProject { project ->
            if (project.rootProject != project) {
                return
            }

            project.buildscript.with {
                dependencies {
                    classpath "de.interactive_instruments:xtraplatform-build:${version}"
                }
            }
        }

        settings.with {
            pluginManagement {
                repositories {
                    maven {
                        url "https://dl.interactive-instruments.de/repository/maven-releases/"
                    }
                    maven {
                        url "https://dl.interactive-instruments.de/repository/maven-snapshots/"
                    }
                    gradlePluginPortal()
                }

                plugins {
                    id "de.interactive_instruments.xtraplatform-layer" version "${version}"
                    id "de.interactive_instruments.xtraplatform-application" version "${version}"
                    id "de.interactive_instruments.xtraplatform-module" version "${version}"
                    id "de.interactive_instruments.xtraplatform-doc" version "${version}"
                    id "de.interactive_instruments.xtraplatform-composite" version "${version}"
                }
            }

            dependencyResolutionManagement {
                repositories {
                    maven {
                        url "https://dl.interactive-instruments.de/repository/maven-releases/"
                    }
                    maven {
                        url "https://dl.interactive-instruments.de/repository/maven-snapshots/"
                    }
                }
                versionCatalogs {
                    xtraplatform {
                        from("de.interactive_instruments:xtraplatform-catalog:${version}")
                    }
                }
            }

            extensions.gitHooks.with {
                preCommit { ctx ->
                    ctx.tasks('check', [].toArray(), true)
                }
                createHooks(false)
            }
        }
    }

    static String getVersion(Settings settings) {
        final Configuration classpath = settings.getBuildscript().getConfigurations().getByName("classpath");
        return classpath.getResolvedConfiguration().getResolvedArtifacts().stream()
                .map(artifact -> artifact.getModuleVersion().getId())
                .filter(id -> "de.interactive_instruments".equals(id.getGroup()) && id.getName().startsWith("xtraplatform-"))
                .findAny()
                .map(ModuleVersionIdentifier::getVersion)
                .orElse("+");
    }

}
