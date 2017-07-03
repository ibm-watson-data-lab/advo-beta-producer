#Scenario: LocalCart seeks to boost sales by better understanding their customers

LocalCart is a fast growing, online same-day-delivery grocery store. The business model is based on the ability to supply local products along with routine shopping needs, and to be able to deliver accurately in a small delivery window &emdash; as little as an hour from placing the order.

<!--LocalCart specializes in time-boxed fire sales. The __Friday at Five__ sale is a time and quantity bounded sale, with a limited quantity of popular products available at a special price only between 5 and 6 pm each Friday. The constraints on quantity and time create a pressure to buy.  -->    To be successful, LocalCart requires a deep understanding of who their customers are, the behavior of those customers when shopping through LocalCart, and the ability to provide targeted product recommendations to increase revenue. 


##LocalCart's data analysis needs
LocalCart has identified three key data and analytics projects that they want to start as soon as possible. LocalCart is looking for a single cloud data and analytics platform that will enable them to quickly and easily deliver all three projects. 

LocalCart wants:

* Real-time (streaming) aggregated customer activities with intraday revenue figures and real-time funnel status.
* Customer behavior information, such as demographics, shopping cart values, and so on.
* A recommendation engine to encourage additional purchases through recommendations based on past buying behavior.

Using functionality available in Watson Data Platform, LocalCart's data scientists and application developers create assets in the form of Jupyter notebooks. Notebooks can include text instructions, runnable code, and output including charts and graphs.   <!-- with links to each notebook in the advo beta project -->(links pending beta website)

IBM Watson Data Platform provides LocalCart with a single cloud platform for app deployment, data management, streaming analytics, machine learning, and more. Follow along as we create the deliverables that address LocalCart's needs by using Watson Data Platform.


###Aggregating and analyzing customer activity data

To address LocalCart's first project goal, their data science team collects data from their application clickstream and saves it to a database for analysis. You can simulate the streaming data capture by running the following notebooks:

1. **Streaming data producer**. Build a clickstream event generator to provide streaming data, which is then archived in REDIS for later analytics processing. [Notebook #1: Creating a Kafka Producer of ClickStream events](https://apsportal.ibm.com/analytics/notebooks/c3aee820-01af-478f-bd0f-07d80866863f/view?projectid=81238e6c-a19b-4c5c-9e45-753dfe7b7de3&context=analytics)
1. **Streaming data pipeline**. Build the streaming pipeline that monitors intraday revenue 
	* [Notebook #2: Creating a streaming pipeline](https://apsportal.ibm.com/analytics/notebooks/d06e9e69-f8c9-4608-9e07-deb10fc4f85f/view?projectid=81238e6c-a19b-4c5c-9e45-753dfe7b7de3&context=analytics)
	* [Notebook #2a: Aggregating stream data](https://apsportal.ibm.com/analytics/notebooks/33ff5c0d-9a2a-44df-89fd-32ab2703097e/view?projectid=81238e6c-a19b-4c5c-9e45-753dfe7b7de3&context=analytics)

###Exploring and analyzing customer activity data

Next the LocalCart team explores learns more about their customers by examining their demographics, and then digs into sales transactions. Developing customer behavior analytics requires two separate but related skillsets: the data engineer, and the data scientist.

You can recreate their analysis by running the following notebooks:

1. **Customer data exploration and analysis**. Build an analysis that ingests and prepares data for the team using Data Refinery and transforms the data for use by the data scientists. 	* [Notebook #3: Static clickstream analysis](https://apsportal.ibm.com/analytics/notebooks/79e5cc81-a452-4039-943a-3dbd08cadb89/view?projectid=81238e6c-a19b-4c5c-9e45-753dfe7b7de3&context=analytics)
	* [Notebook #3: Explore customer demographics](https://apsportal.ibm.com/analytics/notebooks/4a140569-b36f-4c89-9f46-950dbf771503/view?projectid=81238e6c-a19b-4c5c-9e45-753dfe7b7de3&context=analytics)
   * Analyze customer segmentation, average cart value per customer, and average shopping interval by customer segment.Share the graph and chart with other team members.[Notebook #3: Explore sales transactions](https://apsportal.ibm.com/analytics/notebooks/8739b6d6-401f-492c-a707-8d3e9ce43a2d/view?projectid=81238e6c-a19b-4c5c-9e45-753dfe7b7de3&context=analytics)


1. **Customer activity dashboard** Create a dashboard that visualizes real-time aggregated data from clickstream, including:
	* Aggregated customer activities  
	* Intraday revenue figures on 5 minute chart  
	* Real-time funnel status
	[Notebook #4: Visualize streaming data](https://apsportal.ibm.com/analytics/notebooks/d9fd6d78-d55f-4e83-b8ae-d465f7af256f/view?projectid=81238e6c-a19b-4c5c-9e45-753dfe7b7de3&context=analytics)


###Recommending additional purchases

Finally, build a recommendation engine that will encourage LocalCart customers to buy additional products. Recommendations will be based on individual customer buying behavior, all-customers buying behaviors across the LocalCart business, and product rating. 

* **A recommendation engine using Watson Machine Learning**. [Notebook #5: Predicting purchases](https://apsportal.ibm.com/analytics/notebooks/23a722e4-fa68-4412-8c8b-3b4e93977567/view?projectid=81238e6c-a19b-4c5c-9e45-753dfe7b7de3&context=analytics).

The developer or data scientist: 

1. Builds, trains, and tests a recommendation engine model with SparkML.
1. Deploys the model, which generates an API that can be called by the LocalCart application.
1. Runs the deployed model API.
1. Presents results in a simulated application.



  




