build:
	javac -d ./bin src/**/*.java

run:
	java -cp ./bin src/graphicsAndInput/RunGame.java

runtest:
	java -cp ./bin src/graphicsAndInput/TestRunLevel.java

clean:
	rm -rf ./bin
