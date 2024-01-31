import { defineConfig } from "vitepress";
import { readGradleMetadata } from "./gradle";

const gradleMetadata = readGradleMetadata();

export default defineConfig({
  base: `/gradle-java-template/docs/${gradleMetadata.docsVersion}`,
  outDir: "build/vitepress-dist",
  lang: "en-US",
  title: "Gradle Java Template",
  description: "Template for building Java projects with Gradle",
  lastUpdated: true,
  vite: {},
  cleanUrls: true,
  themeConfig: {
    nav: [
      { text: "Home", link: "/" },
      { text: "Examples", link: "/markdown-examples" }
    ],
    sidebar: [
      {
        text: "Examples",
        items: [
          { text: "Markdown Examples", link: "/markdown-examples" },
          { text: "Runtime API Examples", link: "/api-examples" }
        ]
      }
    ],
    socialLinks: [
      { icon: "github", link: "https://github.com/vuejs/vitepress" }
    ]
  }
});
