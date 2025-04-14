export default {
  preset: 'ts-jest/presets/default-esm',
  extensionsToTreatAsEsm: ['.ts'],
  moduleNameMapper: {
    '^(\\.{1,2}/.*)\\.js$': '$1'
  },
  transform: {
    '^.+\\.tsx?$': ['ts-jest', { useESM: true }]
  },
  rootDir: '/home/ubuntu',
  roots: ['/home/ubuntu'],
  testMatch: ['**/lambda.test.ts'],
  testPathIgnorePatterns: ['/node_modules/', '/repos/'],
  haste: {
    enableSymlinks: false,
    forceNodeFilesystemAPI: true,
    throwOnModuleCollision: false
  },
  watchPathIgnorePatterns: ['/repos/'],
  modulePathIgnorePatterns: ['/repos/']
};
