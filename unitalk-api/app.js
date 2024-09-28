// app.js

const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');
const AiPromptResponsePair = require('./AiPromptResponsePair');

const app = express();
const PORT = 3000;

// Middleware
app.use(cors());
app.use(bodyParser.json());

const promptResponseList = [];

// POST endpoint to generate a response
app.post('/generate', (req, res) => {
    const { prompt } = req.body;

    // Here, you would integrate your AI logic to generate a response
    // For demonstration purposes, we'll mock a response
    const response = `Generated response for: ${prompt}`;
    
    // Create a new AiPromptResponsePair instance
    const pair = new AiPromptResponsePair(prompt, response);
    promptResponseList.push(pair);

    res.status(201).json(pair);
});

// GET endpoint to retrieve all responses
app.get('/responses', (req, res) => {
    res.status(200).json(promptResponseList);
});

// Start the server
app.listen(PORT, () => {
    console.log(`Server is running on http://localhost:${PORT}`);
});
 
