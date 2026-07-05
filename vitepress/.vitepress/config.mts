import { defineConfig } from 'vitepress'

export default defineConfig({
  title: "Forgotten Cantrips",
  description: "Documentation for the Mana and Artifice Forgotten Cantrips addon",
  base: '/MnA_Extra_Cantrips/',
  ignoreDeadLinks: true,
  themeConfig: {
    nav: [
      { text: 'Home', link: '/' },
      { text: 'Get Started', link: '/get-started/introduction' },
      { text: 'Product', link: '/product/user-stories' },
      { text: 'Quality', link: '/quality/quality-requirements' },
      { text: 'Process', link: '/process/requirements' },
      { text: 'Reports', link: '/reports/week5' }
    ],

    sidebar: [
      {
        text: 'Get Started',
        items: [
          { text: 'Introduction', link: '/get-started/introduction' },
          { text: 'Demo', link: '/get-started/demo' },
        ]
      },
      {
        text: 'Product',
        items: [
          { text: 'User Stories', link: '/product/user-stories' },
          { text: 'Roadmap', link: '/product/roadmap' },
          { text: 'Definition of Done', link: '/product/definition-of-done' },
          { text: 'Changelog', link: '/product/changelog' }
        ]
      },
      {
        text: 'Architecture',
        items: [
          { text: 'Overview', link: '/architecture/' }
        ]
      },
      {
        text: 'Quality & Testing',
        items: [
          { text: 'Quality Requirements', link: '/quality/quality-requirements' },
          { text: 'Quality Requirement Tests', link: '/quality/quality-requirement-tests' },
          { text: 'Testing', link: '/quality/testing' },
          { text: 'User Acceptance Tests', link: '/quality/user-acceptance-tests' }
        ]
      },
      {
        text: 'Process',
        items: [
          { text: 'Process Requirements', link: '/process/requirements' }
        ]
      },
      {
        text: 'Reports',
        items: [
          { text: 'Week 2', link: '/reports/week2' },
          { text: 'Week 3', link: '/reports/week3' },
          { text: 'Week 4', link: '/reports/week4' },
          { text: 'Week 5', link: '/reports/week5' }
        ]
      }
    ],

    socialLinks: [
      { icon: 'github', link: 'https://github.com/Ultrad00d/MnA_Extra_Cantrips' }
    ]
  }
})
