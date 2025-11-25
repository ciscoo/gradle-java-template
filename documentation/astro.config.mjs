// @ts-check
import { defineConfig } from "astro/config";
import starlight from "@astrojs/starlight";

export default defineConfig({
  integrations: [
    starlight({
      title: "Example",
      social: [
        {
          icon: "open-book",
          label: "Javadoc",
          // Works
          href: "/javadoc/index.html",
        },
      ],
      sidebar: [
        {
          label: "Javadoc",
          // Does not work
          link: "/javadoc"
        },
      ],
    }),
  ],
});
