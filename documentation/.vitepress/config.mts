import { defineConfig } from "vitepress";
import { readGradleMetadata } from "./gradle.mjs";

const gradleMetadata = readGradleMetadata();

// https://vitepress.dev/reference/site-config
export default defineConfig({
  base: `/gradle-java-template`,
  outDir: "build/vitepress-dist",
  lastUpdated: true,
  cleanUrls: true,
  title: "Gradle Java Template",
  description: "A VitePress Site",
  vite: {
    define: {
      __PROJECT_VERSION__: `"${gradleMetadata.version}"`,
      __GRADLE_VERSION__: `"${gradleMetadata.gradleVersion}"`,
    },
  },
  themeConfig: {
    // https://vitepress.dev/reference/default-theme-config
    nav: [
      { text: "Home", link: "/" },
      { text: "Examples", link: "/markdown-examples" },
    ],

    sidebar: [
      {
        text: "Examples",
        items: [
          { text: "Markdown Examples", link: "/markdown-examples" },
          { text: "Runtime API Examples", link: "/api-examples" },
        ],
      },
    ],

    socialLinks: [
      { icon: "github", link: "https://github.com/vuejs/vitepress" },
    ],
  },
});
