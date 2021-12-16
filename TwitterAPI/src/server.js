const express = require('express')
const app = express()
const swaggerUi = require('swagger-ui-express')
const swaggerJsondoc = require('swagger-jsdoc')

// CONFIGURACION
app.set('puerto', process.env.PORT || 3000)

// MIDDLEWARES
app.use(express.json())

// RUTAS
app.use(require('./rutas/TipoUsuario.js'))
app.use(require('./rutas/Usuario.js'))
app.use(require('./rutas/Tweet.js'))
app.use(require('./rutas/Seguidor.js'))
app.use(require('./rutas/Login.js'))
app.use(require('./rutas/Likes.js'))

// DOCUMENTACION SWAGGER
const options = {
  swaggerDefinition: {
    openapi: '3.0.0',
    info: {
      title: 'TwitterAPI',
      version: '1.0.0',
      description:
          'API para proyecto de sistemas en red'
    },
    servers: [
      {
        url: 'http://localhost:3000/'
      }
    ]
  },
  apis: ['../src/rutas/*.js']
}

const specs = swaggerJsondoc(options)

app.use('/docs', swaggerUi.serve, swaggerUi.setup(specs))

// INICIAR SERVIDOR
app.listen(app.get('puerto'), () => {
  console.log('SERVIDOR EN PUERTO', app.get('puerto'))
})
