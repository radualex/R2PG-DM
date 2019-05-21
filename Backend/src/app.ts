import express = require('express');

const app: express.Application = express();

app.get('/', function (req, res) {
  res.send('Hello World!');
});

const PORT = process.env.PORT || 3000;

app.listen(3000, function () {
  console.log(`Server is running in http://localhost:${PORT}`);
});