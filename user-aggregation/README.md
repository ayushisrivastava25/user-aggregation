<h1>User Event Aggregation CLI</h1>

The following commands need to be run after specifying the input file: input.json

For example:

Part 1:

input.json

`[
{"userId": 1, "eventType": "post", "timestamp": 1672444800},
{"userId": 1, "eventType": "likeReceived", "timestamp": 1672444801},
{"userId": 1, "eventType": "likeReceived", "timestamp": 1672444802},
{"userId": 1, "eventType": "post", "timestamp": 1672531200},
{"userId": 2, "eventType": "comment", "timestamp": 1672531201},
{"userId": 2, "eventType": "post", "timestamp": 1672531202}
]`

Shell command to be run:

`aggregate-events input.json output.json create`

Check the output of output.json

Part 2:

Make the required changes in the file: input.json

Run the following command in the shell

`aggregate-events input.json output.json update`

Execution Steps for the current output.json file:

Step 1: input.json

`[
{"userId": 1, "eventType": "post", "timestamp": 1672444800},
{"userId": 1, "eventType": "likeReceived", "timestamp": 1672444801},
{"userId": 1, "eventType": "likeReceived", "timestamp": 1672444802},
{"userId": 1, "eventType": "post", "timestamp": 1672531200},
{"userId": 2, "eventType": "comment", "timestamp": 1672531201},
{"userId": 2, "eventType": "post", "timestamp": 1672531202}
]`

Shell Command:

Step 2: `aggregate-events input.json output.json create`

Step 3: `aggregate-events input.json output.json update`

Step 4: `aggregate-events input.json output.json update`

Current output.json Output:

`[
{"userId": 1, "date": "2022-12-31", "post": 3, "likesReceived": 6},
{"userId": 1, "date": "2023-01-01", "post": 3},
{"userId": 2, "date": "2023-01-01", "post": 3, "comment": 3}
]`

input.json was not modified before Steps 3 and 4, it remained exactly same as Step 1.