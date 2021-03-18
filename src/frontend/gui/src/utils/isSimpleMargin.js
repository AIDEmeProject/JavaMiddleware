function isSimpleMargin(configuration) {
  return (
    configuration.activeLearner.name &&
    configuration.activeLearner.name === "SimpleMargin"
  );
}

export default isSimpleMargin;
