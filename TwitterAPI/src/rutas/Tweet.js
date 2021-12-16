const express = require('express')
const ruta = express.Router()
const mysqlConnection = require('../database')
const verificarToken = require('../auth')

/**
 * @swagger
 * components:
 *  schemas:
 *    Tweet:
 *      type: object
 *      properties:
 *        idTweet:
 *          type: integer
 *          description: id autogenerado de Tweet
 *        Cuerpo:
 *          type: string
 *          description: Contenido del tweet
 *        FechaHoraPublicacion:
 *          type: string
 *          description: Fecha en la que se publico o actualizo el Tweet
 *        Likes:
 *          type: integer
 *          description: cantidad de likes del Tweet, comienza en 0
 *        idUsuario:
 *          type: integer
 *          description: id del Usuario que publico el Tweet
 *      required:
 *        - Cuerpo
 *        - FechaHoraPublicacion
 *        - idUsuario
 *  parameters:
 *    idTweet:
 *      in: path
 *      name: idTweet
 *      required: true
 *      schema:
 *        type: integer
 *      description: id del Tweet
 *    idUsuario:
 *      in: path
 *      name: idUsuario
 *      required: true
 *      schema:
 *        type: integer
 *      description: id del Usuario
 *    Keyword:
 *      in: path
 *      name: Keyword
 *      required: true
 *      schema:
 *        type: string
 *      description: cadena para buscar Tweets que tengan relacion
 */

/**
 * @swagger
 * tags:
 *  name: Tweet
 *  description: endpoints para Tweet
 */

/**
 * @swagger
 * /Tweet:
 *  get:
 *    summary: Permite consultar la lista de Tweet que hay en el sistema
 *    tags: [Tweet]
 *    responses:
 *      200:
 *        description: Lista de Tweet
 *        content:
 *          application/json:
 *            schema:
 *              type: array
 *              items:
 *                $ref: '#/components/schemas/Tweet'
 */
ruta.get('/Tweet', verificarToken, (req, res) => {
  mysqlConnection.query('CALL R_Tweet()', (err, rows, fields) => {
    if (!err) {
      res.json(rows[0])
    } else {
      res.status(500)
    }
  })
})

/**
 * @swagger
 * /TweetByID/{idTweet}:
 *  get:
 *    summary: Permite obtener un Tweet especifico
 *    tags: [Tweet]
 *    responses:
 *      200:
 *        description: retorna el Tweet encontrado
 *        content:
 *          application/json:
 *            schema:
 *              type: object
 *              items:
 *                $ref: '#/components/schemas/Tweet'
 */
ruta.get('/TweetByID/:idTweet', verificarToken, (req, res) => {
  const { idTweet } = req.params
  mysqlConnection.query('CALL R_TweetByID(?)', [idTweet], (err, rows, fields) => {
    if (!err) {
      res.json(rows[0][0])
    } else {
      res.status(500)
      console.log(err)
    }
  })
})

/**
 * @swagger
 * /TweetPerfil/{idUsuario}:
 *  get:
 *    summary: Permite consultar la lista de Tweets de un perfil
 *    tags: [Tweet]
 *    responses:
 *      200:
 *        description: Retorna lista de Tweets de un perfil
 *        content:
 *          application/json:
 *            schema:
 *              type: array
 *              items:
 *                $ref: '#/components/schemas/Tweet'
 */
ruta.get('/TweetPerfil/:idUsuario', verificarToken, (req, res) => {
  const { idUsuario } = req.params
  mysqlConnection.query('CALL R_TweetsPerfil(?)', [idUsuario], (err, rows, fields) => {
    if (!err) {
      res.json(rows[0])
    } else {
      res.status(500)
      console.log(err)
    }
  })
})

/**
 * @swagger
 * /Tweet/{idUsuario}:
 *  get:
 *    summary: Permite consultar la lista de Tweets del Usuario mismo dado y de las personas que sigue
 *    tags: [Tweet]
 *    parameters:
 *      - $ref: '#/components/parameters/idUsuario'
 *    responses:
 *      200:
 *        description: Retorna lista de Tweets de un perfil
 *        content:
 *          application/json:
 *            schema:
 *              type: array
 *              items:
 *                $ref: '#/components/schemas/Tweet'
 */
ruta.get('/Tweet/:idUsuario', verificarToken, (req, res) => {
  const { idUsuario } = req.params
  mysqlConnection.query('CALL R_TweetFollowing(?)', [idUsuario], (err, rows, fields) => {
    if (!err) {
      res.json(rows[0])
    } else {
      res.status(500)
    }
  })
})

/**
 * @swagger
 * /Tweet/Content/{Keyword}:
 *  get:
 *    summary: Retorna la lista de Tweets que contengan la cadena dada
 *    tags: [Tweet]
 *    parameters:
 *      - $ref: '#/components/parameters/Keyword'
 *    responses:
 *      200:
 *        description: Retorna lista de Tweets de un perfil
 *        content:
 *          application/json:
 *            schema:
 *              type: array
 *              items:
 *                $ref: '#/components/schemas/Tweet'
 */
ruta.get('/Tweet/Content/:Keyword', verificarToken, (req, res) => {
  const { Keyword } = req.params
  mysqlConnection.query('CALL S_InTweet(?)', [Keyword], (err, rows, fields) => {
    if (!err) {
      res.status(200).json(rows[0])
    } else {
      res.status(500)
    }
  })
})

/**
 * @swagger
 * /Tweet:
 *  post:
 *    summary: Crear un nuevo Tweet
 *    tags: [Tweet]
 *    requestBody:
 *      required: true
 *      content:
 *        application/json:
 *          schema:
 *            $ref: '#/components/schemas/Tweet'
 *    responses:
 *      201:
 *        description: Se creo o no el Tweet
 *        content:
 *          application/json:
 *            schema:
 *              type: object
 *              properties:
 *                Respuesta:
 *                  type: string
 *                  description: Y si se creo, N si lo no
 *
 */
ruta.post('/Tweet', verificarToken, (req, res) => {
  const { Cuerpo, FechaHoraPublicacion, Multimedia, idUsuario } = req.body
  mysqlConnection.query('CALL CU_Tweet(?, ?, ?, ?, ?)', [0, Cuerpo, FechaHoraPublicacion, Multimedia, idUsuario], (err, rows, fields) => {
    if (!err) {
      res.status(201).json(rows[0][0])
    } else {
      res.status(500)
      console.log(err)
    }
  })
})

/**
 * @swagger
 * /Tweet/{idTweet}:
 *  put:
 *    summary: Permite actualizar un Tweet especifico
 *    tags: [Tweet]
 *    parameters:
 *      - $ref: '#/components/parameters/idTweet'
 *    requestBody:
 *      required: true
 *      content:
 *        application/json:
 *          schema:
 *            $ref: '#/components/schemas/Tweet'
 *    responses:
 *      200:
 *        description: Se actualiza un Tweet
 *        content:
 *          application/json:
 *            schema:
 *              type: object
 *              properties:
 *                Respuesta:
 *                  type: string
 *                  description: A si se actualizo, N si no
 */
ruta.put('/Tweet/:idTweet', verificarToken, (req, res) => {
  const { idTweet } = req.params
  const { Cuerpo, FechaHoraPublicacion, idUsuario } = req.body
  mysqlConnection.query('CALL CU_Tweet(?, ?, ?, ?)', [idTweet, Cuerpo, FechaHoraPublicacion, idUsuario], (err, rows, fields) => {
    if (!err) {
      res.status(200).json(rows[0][0])
    } else {
      res.status(500)
    }
  })
})

/**
 * @swagger
 * /Tweet/{idTweet}:
 *  delete:
 *    summary: Eliminar un Tweet de la BD por id
 *    tags: [Tweet]
 *    parameters:
 *      - $ref: '#/components/parameters/idTweet'
 *    responses:
 *      200:
 *        description: Tweet eliminado o no
 *        content:
 *          application/json:
 *            schema:
 *              type: object
 *              properties:
 *                Respuesta:
 *                  type: string
 *                  description: Y si se elimino, N si no
 */
ruta.delete('/Tweet/:idTweet', verificarToken, (req, res) => {
  const { idTweet } = req.params
  mysqlConnection.query('CALL D_Tweet(?)', [idTweet], (err, rows, fields) => {
    if (!err) {
      res.json(rows[0][0])
    } else {
      res.status(500)
    }
  })
})

module.exports = ruta
