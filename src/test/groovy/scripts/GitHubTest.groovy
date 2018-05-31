package scripts

import org.junit.BeforeClass
import org.junit.Test
import org.jvnet.hudson.test.recipes.LocalData
import org.jvnet.hudson.test.recipes.WithPlugin
import utilities.ZipTestFiles

import static org.hamcrest.CoreMatchers.containsString
import static org.hamcrest.CoreMatchers.equalTo
import static org.junit.Assert.assertThat

class GitHubTest extends StartupTest {

    @BeforeClass
    public static void setUp() {
        setUp(GitHubTest.class, ["scripts/credentials.groovy", "scripts/github.groovy"])
    }

    @Test
    @LocalData
    @WithPlugin(["github-branch-source-2.3.4.hpi", "github-1.28.1.hpi", "credentials-2.1.16.hpi",
            "display-url-api-2.2.0.hpi", "git-3.7.0.hpi", "github-api-1.90.hpi", "scm-api-2.2.6.hpi",
            "structs-1.14.hpi", "git-client-2.7.0.hpi", "mailer-1.20.hpi", "matrix-project-1.12.hpi",
            "ssh-credentials-1.13.hpi", "apache-httpcomponents-client-4-api-4.5.3-2.1.hpi", "jsch-0.1.54.1.hpi",
            "junit-1.23.hpi", "script-security-1.44.hpi", "workflow-api-2.27.hpi", "workflow-step-api-2.14.hpi",
            "workflow-scm-step-2.6.hpi", "jackson2-api-2.8.10.1.hpi", "plain-credentials-1.4.hpi",
            "token-macro-2.3.hpi", "workflow-job-2.21.hpi", "workflow-support-2.17.hpi", "branch-api-2.0.19.hpi",
            "cloudbees-folder-6.4.hpi", "workflow-multibranch-2.19.hpi", "workflow-multibranch-2.19.hpi",
            "workflow-cps-2.53.hpi", "ace-editor-1.0.1.hpi", "jquery-detached-1.2.1.hpi"
    ])
    @ZipTestFiles(files = ["jenkins.config"])
    void shouldConfigureGitHub() {

        // github.com
        def githubOrganisation = jenkinsRule.jenkins.getItem("buildit")
        assertThat(githubOrganisation.displayName, equalTo("Buildit"))
        assertThat(githubOrganisation.name, equalTo("buildit"))
        assertThat(githubOrganisation.description, equalTo("Buildit Github Organisation"))

        def projectFactories = githubOrganisation.getProjectFactories()
        assertThat(projectFactories.size(), equalTo(1))

        def projectFactory0 = projectFactories[0]
        assertThat(projectFactory0.scriptPath, equalTo('Jenkinsfile'))

        // Github Enterprise
        def xmlConfig = new File(jenkinsRule.jenkins.root.getAbsoluteFile(), "org.jenkinsci.plugins.github_branch_source.GitHubConfiguration.xml").text
        assertThat(xmlConfig, containsString("<apiUri>https://api.github.mycompany.com</apiUri>"))
        assertThat(xmlConfig, containsString("<name>MyCompany</name>"))

        def gheOrganisation = jenkinsRule.jenkins.getItem("mycompany")
        assertThat(gheOrganisation.displayName, equalTo("MyCompany"))
        assertThat(gheOrganisation.name, equalTo("mycompany"))
        assertThat(gheOrganisation.description, equalTo("MyCompany Github Enterprise Organisation"))

        def gheProjectFactories = gheOrganisation.getProjectFactories()
        assertThat(gheProjectFactories.size(), equalTo(2))

        def gheProjectFactory0 = gheProjectFactories[0]
        assertThat(gheProjectFactory0.scriptPath, equalTo('Jenkinsfile.prod'))

        def gheProjectFactory1 = gheProjectFactories[1]
        assertThat(gheProjectFactory1.scriptPath, equalTo('Jenkinsfile.test'))
    }

}
