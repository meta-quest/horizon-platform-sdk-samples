// @ts-check

/** @type {import('@docusaurus/types').Config} */
const config = {
  title: 'Horizon Platform SDK Samples',
  tagline: 'Android sample apps for the Horizon Platform SDK',
  url: 'https://meta-quest.github.io',
  baseUrl: '/horizon-platform-sdk-samples/',
  organizationName: 'meta-quest',
  projectName: 'horizon-platform-sdk-samples',
  onBrokenLinks: 'throw',
  onBrokenMarkdownLinks: 'warn',

  presets: [
    [
      'classic',
      /** @type {import('@docusaurus/preset-classic').Options} */
      ({
        docs: {
          sidebarPath: require.resolve('./sidebars.js'),
        },
      }),
    ],
  ],
};

module.exports = config;
