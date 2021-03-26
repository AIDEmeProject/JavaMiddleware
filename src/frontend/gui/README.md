# AIDEMe Frontend

The frontend is built with [React.js](https://reactjs.org/), a frontend library developped by Facebook and allowing to build composable components. Each part of the interface is represented by a component.

The plotting is done with [D3.js](https://d3js.org/).

The styling is done mainly with [Boostrap](https://getbootstrap.com/).

## Setup and run the development server

- Install [`npm`](https://docs.npmjs.com/downloading-and-installing-node-js-and-npm)

- Install packages

```
cd src/frontend/gui
npm install
```

- Start the developpement server

```
npm start
```

- Stop the developpement server: Ctrl + C

## Code editing

- Linter: eslint (to use `eslint` with VSCode, see for example [https://marketplace.visualstudio.com/items?itemName=dbaeumer.vscode-eslint](https://marketplace.visualstudio.com/items?itemName=dbaeumer.vscode-eslint))

- Formatter: Prettier (see `.vscode/settings.json` for a config example)

## Folder structure

**actions** folder contains functions to call the backend (currently java)

**components** folder contains react components

**constants** folder contains algorithm configuration

**model** folder contains some classes containing logic (manipulating dataset)

**lib** folder contains some utilitaties functions

**ressources** image and other ressources

## Old version

The old version of the code is in the `v2-dsm-fix` branch.
