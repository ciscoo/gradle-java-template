import { readFileSync } from "fs";

type GradleMetadata = {
  version: string;
  gradleVersion: string;
};

export function readGradleMetadata(): GradleMetadata {
  const contents = readFileSync("build/gradle-project-metadata.json", "utf-8");
  return JSON.parse(contents);
}
