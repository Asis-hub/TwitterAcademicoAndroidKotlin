const express = require('express')
const ruta = express.Router()
const mysqlConnection = require('../database')
const verificarToken = require('../auth')

/**
 * @swagger
 * components:
 *  schemas:
 *    Likes:
 *      type: object
 *      properties:
 *        idTweet:
 *          type: integer
 *          description: id del Tweet al que se le da like
 *        idUsuario:
 *          type: integer
 *          description: id del Usuario que le da like a un Tweet
 *      required:
 *        - idTweet
 *        - idUsuario
 *  parameters:
 *    idTweet:
 *      in: path
 *      name: idTweet
 *      required: true
 *      schema:
 *        type: integer
 *      description: id del Tweet al que se le da like
 *    idUsuario:
 *      in: path
 *      name: idUsuario
 *      required: true
 *      schema:
 *        type: integer
 *      description: id del Usuario que da like al un Tweet
 */
/**
 * @swagger
 * tags:
 *  name: Likes
 *  description: Endpoints para Likes
 */

/**
 * @swagger
 * /Likes/{idTweet}/{idUsuario}:
 *  get:
 *    summary: Verifica si un usuario le dio like a un Tweet dado
 *    tags: [Likes]
 *    parameters:
 *      - $ref: '#/components/parameters/idTweet'
 *      - $ref: '#/components/parameters/idUsuario'
 *    responses:
 *      200:
 *        description: es like esta dado o no
 *        content:
 *          application/json:
 *            schema:
 *              type: object
 *              properties:
 *                Respuesta:
 *                  type: string
 *                  description: Y si esta es Like, N si lo esta
 */
ruta.get('/Likes/:idTweet/:idUsuario', verificarToken, (req, res) => {
  const { idTweet, idUsuario } = req.params
  mysqlConnection.query('CALL R_IsLiked(?,?)', [idTweet, idUsuario], (err, rows, fields) => {
    if (!err) {
      res.status(200).json(rows[0][0])
    } else {
      res.status(500)
    }
  })
})

/**
 * @swagger
 * /Likes/{idTweet}:
 *  get:
 *    summary: Obtiene la catidad de likes que tiene un Tweet
 *    tags: [Likes]
 *    parameters:
 *      - $ref: '#/components/parameters/idTweet'
 *    responses:
 *      200:
 *        description: Cantidad de Tweets
 *        content:
 *          application/json:
 *            schema:
 *              type: object
 *              properties:
 *                Cantidad:
 *                  type: integer
 *                  description: Cantidad de likes del Tweet dado
 */
ruta.get('/Likes/:idTweet', verificarToken, (req, res) => {
  const { idTweet } = req.params
  mysqlConnection.query('CALL R_CantidadLikes(?)', [idTweet], (err, rows, fields) => {
    if (!err) {
      res.status(200).json(rows[0][0])
    } else {
      res.status(500)
    }
  })
})


/**
 * @swagger
 * /Likes:
 *  post:
 *    summary: Permite dar like a un Tweet
 *    tags: [Likes]
 *    requestBody:
 *      required: true
 *      content:
 *        application/json:
 *          schema:
 *            $ref: '#/components/schemas/Likes'
 *    responses:
 *      200:
 *        description: El like se dio o no
 *        content:
 *          application/json:
 *            schema:
 *              type: object
 *              properties:
 *                Respuesta:
 *                  type: string
 *                  description: Y si se ejecuto, N si no lo hizo
 */
ruta.post('/Likes', verificarToken, (req, res) => {
  const { idTweet, idUsuario } = req.body
  mysqlConnection.query('CALL C_Likes(?, ?)', [idTweet, idUsuario], (err, rows, fields) => {
    if (!err) {
      res.status(201).json(rows[0][0])
    } else {
      res.status(500)
    }
  })
})

/**
 * @swagger
 * /Likes/{idTweet}/{idUsuario}:
 *  delete:
 *    summary: Quita el like de un Usuario a un Tweet dado
 *    tags: [Likes]
 *    parameters:
 *      - $ref: '#/components/parameters/idTweet'
 *      - $ref: '#/components/parameters/idUsuario'
 *    responses:
 *      200:
 *        description: Se quito o no el Tweet
 *        content:
 *          application/json:
 *            schema:
 *              type: object
 *              properties:
 *                Respuesta:
 *                  type: string
 *                  description: Y si se quito, N si lo quito
 */
ruta.delete('/Likes/:idTweet/:idUsuario', verificarToken, (req, res) => {
  const { idTweet, idUsuario } = req.params
  mysqlConnection.query('CALL D_Likes(?, ?)', [idTweet, idUsuario], (err, rows, fields) => {
    if (!err) {
      res.status(200).json(rows[0][0])
    } else {
      res.status(500)
    }
  })
})
module.exports = ruta
