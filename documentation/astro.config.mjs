// @ts-check
import { defineConfig } from "astro/config";
import starlight from "@astrojs/starlight";

export default defineConfig({
  outDir: "build/dist",
  integrations: [
    starlight({
      title: "Example",
      social: [
        {
          icon: "open-book",
          label: "Javadoc",
          href: "/javadoc/index.html",
        },
      ],
      sidebar: [
        {
          label: "User Guide",
          autogenerate: { directory: "user-guide" },
        },
      ],
    }),
  ],
});
