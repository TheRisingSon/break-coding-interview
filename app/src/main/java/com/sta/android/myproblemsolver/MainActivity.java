package com.sta.android.myproblemsolver;

import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    interface Clearable {
        void clearMe();
    }

    private ArrayList<Clearable> clearables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Object nullObject = null;
        nullObject.hashCode();

        TextView textView = new TextView(this);
        Spinner spinner = textView;

        callNonexistentMethod();

        clearables = new ArrayList<>();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Clearable clearable : clearables) {
                    clearable.clearMe();
                }
                clearables.clear();
                Snackbar.make(view, "All cleared!!", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    HashMap<Integer, Integer> problem9_1_mem = new HashMap<>();

    /**
     * Problem 9.1 - page 109
     * @param numOfSteps
     * @return Num of possible ways the child can run up the stair
     */
    private int doProblem9_1(int numOfSteps) {
        int ways = 0;
        if (numOfSteps >= 3) {
            ways += problem9_1_StepBackward(numOfSteps, 3);
        }
        if (numOfSteps >= 2) {
            ways += problem9_1_StepBackward(numOfSteps, 2);
        }
        if (numOfSteps >= 1) {
            ways += problem9_1_StepBackward(numOfSteps, 1);
        }
        return ways;
    }

    private int problem9_1_StepBackward(int remainingSteps, int stepsTaken) {
        if (problem9_1_mem.containsKey(remainingSteps)) {
            return problem9_1_mem.get(remainingSteps);
        }

        int ways = 0;

        if (stepsTaken == 3 && remainingSteps >= 3) {
            remainingSteps -= 3;
            ways++;
        } else if (stepsTaken == 2 && remainingSteps >= 2) {
            remainingSteps -= 2;
            ways++;
        } else if (remainingSteps >= 1) {
            remainingSteps -= 1;
            ways++;
        }

        int moreWays = 0;
        if (remainingSteps >= 3) {
            moreWays += problem9_1_StepBackward(remainingSteps, 3);
        }
        if (remainingSteps >= 2) {
            moreWays += problem9_1_StepBackward(remainingSteps, 2);
        }
        if (remainingSteps >= 1) {
            moreWays += problem9_1_StepBackward(remainingSteps, 1);
        }
        if (moreWays > 1) {
            ways *= moreWays;  /** This is the culprit **/
        }

        problem9_1_mem.put(remainingSteps, ways);

        return ways;
    }

    /**
     * Solution for prob 9.1
     *
     * @param n
     * @return
     */
    private int problem9_1_Sol_CountWays(int n) {
        if (n < 0) {
            return 0;
        } else if (n == 0) {
            return 1;
        } else if (problem9_1_mem.containsKey(n)) {
            return problem9_1_mem.get(n);
        } else {
            int temp =  problem9_1_Sol_CountWays(n -1) + problem9_1_Sol_CountWays(n - 2) + problem9_1_Sol_CountWays(n - 3);
            problem9_1_mem.put(n, temp);
            return temp;
        }
    }

    public void runProblem9_1(View v) {
        int input = Integer.parseInt(((EditText) findViewById(R.id.et_probl9_1_input)).getText().toString());
        long startTime = System.currentTimeMillis();
        int result = problem9_1_Sol_CountWays(input);
        long runtime = System.currentTimeMillis() - startTime;
        ((TextView) findViewById(R.id.tv_probl9_1_output)).setText("" + result);
        ((TextView) findViewById(R.id.tv_probl9_1_runtime)).setText("" + runtime);
        clearables.add(new Clearable() {
            @Override
            public void clearMe() {
                ((EditText) findViewById(R.id.et_probl9_1_input)).setText(null);
                ((TextView) findViewById(R.id.tv_probl9_1_output)).setText(null);
                ((TextView) findViewById(R.id.tv_probl9_1_runtime)).setText(null);
            }
        });
    }


    //------------------ Prob 9.2 ----------------------

    HashMap<String, Integer> problem9_2_mem = new HashMap<>();
    HashMap<String, Boolean> problem9_2_block_coords = new HashMap<>();
    XY foundPath = null;

    private class XY {
        int x;
        int y;
        XY next1 = null;
        XY next2 = null;
        XY prev = null;

        public XY(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals (Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            XY point = (XY) o;

            if (x != point.x) return false;
            if (y != point.y) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = x;
            result = 31 * result + y;
            return result;
        }
    }

    public void runProblem9_2_possible_ways(View v) {
        int inputX = Integer.parseInt(((EditText) findViewById(R.id.et_probl9_2_input_x)).getText().toString());
        int inputY = Integer.parseInt(((EditText) findViewById(R.id.et_probl9_2_input_y)).getText().toString());
        long startTime = System.currentTimeMillis();
        int result = problem9_2_Sol_CountWays(inputX, inputY, false);
        long runtime = System.currentTimeMillis() - startTime;
        ((TextView) findViewById(R.id.tv_probl9_2_output)).setText("Possible ways: " + result);
        ((TextView) findViewById(R.id.tv_probl9_2_runtime)).setText("" + runtime);
        clearables.add(new Clearable() {
            @Override
            public void clearMe() {
                ((EditText) findViewById(R.id.et_probl9_2_input_x)).setText(null);
                ((EditText) findViewById(R.id.et_probl9_2_input_y)).setText(null);
                ((TextView) findViewById(R.id.tv_probl9_2_output)).setText(null);
                ((TextView) findViewById(R.id.tv_probl9_2_runtime)).setText(null);
            }
        });
    }

    public void runProblem9_2_follow_up(View v) {
        int inputX = Integer.parseInt(((EditText) findViewById(R.id.et_probl9_2_input_x)).getText().toString());
        int inputY = Integer.parseInt(((EditText) findViewById(R.id.et_probl9_2_input_y)).getText().toString());
        long startTime = System.currentTimeMillis();
        problem9_2_Sol_CountWays(inputX, inputY, true);
        long runtime = System.currentTimeMillis() - startTime;
        StringBuilder pathSB = new StringBuilder();
        if (foundPath != null) {
            pathSB.append("(" + foundPath.x + "," + foundPath.y + ")");
            while (foundPath.prev != null) {
                pathSB.append(">(" + foundPath.prev.x + "," + foundPath.prev.y + ")");
                foundPath = foundPath.prev;
            }
        } else {
            pathSB.append("No path found");
        }
        ((TextView) findViewById(R.id.tv_probl9_2_output)).setText("Path: " + pathSB.toString());
        ((TextView) findViewById(R.id.tv_probl9_2_runtime)).setText("" + runtime);
        clearables.add(new Clearable() {
            @Override
            public void clearMe() {
                ((EditText) findViewById(R.id.et_probl9_2_input_x)).setText(null);
                ((EditText) findViewById(R.id.et_probl9_2_input_y)).setText(null);
                ((TextView) findViewById(R.id.tv_probl9_2_output)).setText(null);
                ((TextView) findViewById(R.id.tv_probl9_2_runtime)).setText(null);
            }
        });
    }

    public void runProblem9_2_follow_up_sol(View v) {
        int inputX = Integer.parseInt(((EditText) findViewById(R.id.et_probl9_2_input_x)).getText().toString());
        int inputY = Integer.parseInt(((EditText) findViewById(R.id.et_probl9_2_input_y)).getText().toString());
        long startTime = System.currentTimeMillis();

        problem9_2_block_coords.clear();
        problem9_2_block_coords.put("0,1", true);
        problem9_2_block_coords.put("1,2", true);

        ArrayList<XY> pathToFind = new ArrayList<>();
        HashMap<XY, Boolean> cache = new HashMap<>();

        Log.i("mydebug", "runProblem9_2_follow_up_sol");
        boolean success = problem9_2_Sol_getPath(inputX, inputY, pathToFind, cache);

        long runtime = System.currentTimeMillis() - startTime;
        StringBuilder pathSB = new StringBuilder();

        if (success) {
            for (int i = pathToFind.size() - 1; i >= 0; i--) {
                XY xy = pathToFind.get(i);
                pathSB.append(">(" + xy.x + "," + xy.y + ")");
            }
        } else {
            pathSB.append("No path found");
        }
        ((TextView) findViewById(R.id.tv_probl9_2_output)).setText("Path (sol): " + pathSB.toString());
        ((TextView) findViewById(R.id.tv_probl9_2_runtime)).setText("" + runtime);
        clearables.add(new Clearable() {
            @Override
            public void clearMe() {
                ((EditText) findViewById(R.id.et_probl9_2_input_x)).setText(null);
                ((EditText) findViewById(R.id.et_probl9_2_input_y)).setText(null);
                ((TextView) findViewById(R.id.tv_probl9_2_output)).setText(null);
                ((TextView) findViewById(R.id.tv_probl9_2_runtime)).setText(null);
            }
        });
    }

    private int problem9_2_Sol_CountWays(int x, int y, boolean followUP) {
        foundPath = null;
        problem9_2_block_coords.clear();
        problem9_2_block_coords.put("0,1", true);
        problem9_2_block_coords.put("1,2", true);

        XY destNode = new XY(x, y);
        return problem9_2_CountWaysOnXMove(x, y, destNode, followUP);
    }

    private boolean isBlocked(int x, int y) {
        return problem9_2_block_coords.containsKey(x + "," + y) && problem9_2_block_coords.get(x + "," + y);
    }

    private boolean problem9_2_Sol_getPath(int x, int y, ArrayList<XY> path, HashMap<XY, Boolean> cache) {
        XY p = new XY(x, y);

        if (cache.containsKey(p)) { // Already visited this cell
            return cache.get(p);
        }

        path.add(p);

        if (x == 0 && y == 0) {
            return true; // found a path
        }

        boolean success = false;
        if (x >= 1 && !isBlocked(x -1, y)) {
            success = problem9_2_Sol_getPath(x - 1, y, path, cache);
        }
        if (!success && y >= 1 && !isBlocked(x, y - 1)) {
            success = problem9_2_Sol_getPath(x, y - 1, path, cache);
        }

        if (!success) {
            path.remove(p);
        }

        cache.put(p, success);

        return success;
    }

    private int problem9_2_CountWaysOnXMove(int x, int y, XY node, boolean followUp) {
        if (foundPath == null || !followUp) {
            if (x < 0) {
                return 0;
            } else if (x == 0) {
                if (followUp) {
                    if (y <= 0) {
                        foundPath = node;
                    } else if (!isBlocked(x, y - 1)) {
                        node.next2 = new XY(x, y - 1);
                        node.next2.prev = node;
                        problem9_2_CountWaysOnYMove(x, y - 1, node.next2, followUp);
                    }
                }

                return 1;
            } else if (!followUp && problem9_2_mem.containsKey(x + "," + y)) {
                return problem9_2_mem.get(x + "," + y);
            } else {
                int temp = 0;

                if (!isBlocked(x - 1, y)) {
                    node.next1 = new XY(x - 1, y);
                    node.next1.prev = node;
                    temp = problem9_2_CountWaysOnXMove(x - 1, y, node.next1, followUp);
                }

                if (!isBlocked(x, y - 1)) {
                    node.next2 = new XY(x, y - 1);
                    node.next2.prev = node;
                    temp += problem9_2_CountWaysOnYMove(x, y - 1, node.next2, followUp);
                }

                problem9_2_mem.put(x + "," + y, temp);

                return temp;
            }
        }

        return 0;
    }

    private int problem9_2_CountWaysOnYMove(int x, int y, XY node, boolean followUp) {
        if (foundPath == null || !followUp) {
            if (y < 0) {
                return 0;
            } else if (y == 0) {
                if (followUp) {
                    if (x <= 0) {
                        foundPath = node;
                    } else if (!isBlocked(x - 1, y)) {
                        node.next1 = new XY(x - 1, y);
                        node.next1.prev = node;
                        problem9_2_CountWaysOnXMove(x - 1, y, node.next1, followUp);
                    }
                }

                return 1;
            } else if (!followUp && problem9_2_mem.containsKey(x + "," + y)) {
                return problem9_2_mem.get(x + "," + y);
            } else {
                int temp = 0;
                if (!isBlocked(x - 1, y)) {
                    node.next1 = new XY(x - 1, y);
                    node.next1.prev = node;
                    temp = problem9_2_CountWaysOnXMove(x - 1, y, node.next1, followUp);
                }

                if (!isBlocked(x, y - 1)) {
                    node.next2 = new XY(x, y - 1);
                    node.next2.prev = node;
                    temp += problem9_2_CountWaysOnYMove(x, y - 1, node.next2, followUp);
                }

                problem9_2_mem.put(x + "," + y, temp);

                return temp;
            }
        }

        return 0;
    }



    /******************* SCALABILITY & MEMORY LIMIT ***********************/

    // ----------- Problem 10.3 -------------
    private class Problem10_3 {

        final int fourBillions = Integer.MAX_VALUE + 1;
        final long size = fourBillions / 8l;
        ArrayList<Integer> intsInFile = new ArrayList<>();

        public int problem10_3_1GB_Mem_Available() {
            byte[] bitArray = new byte[(int) size];

            for (Integer i : intsInFile) {
                updateNumberExistence(i, 1, bitArray);
            }

            for (int candidate = 0; candidate <= Integer.MAX_VALUE; candidate++) {
                if (!numberExists(candidate, bitArray)) {
                    return candidate;
                }
            }

            return -1;
        }

        private class IntRange {
            public int start;
            public int end;
        }

        int bitSize = 1048576; // 2^20 bits
        int blockNum = 4096; // 2^12
        byte[] bitfield = new byte[bitSize / 8];
        int[] blocks = new int[blockNum];


        public int problem10_3_10BM_Mem_Available() {
            // Pass 1: Go through each integer and update the ranges accordingly
            for (int currentInt : intsInFile) {
                int blockPos = currentInt / blockNum;
                blocks[blockPos] += 1;
            }

            // Pass 2: Update the bit array for int in file what falls within the range
            int rangeMin = -1;
            int rangeMax = -1;
            for (int i = 0; i < blockNum; i++) {
                if (blocks[i] < bitSize) {
                    rangeMin = i * bitSize;
                    rangeMax = i * bitSize + bitSize;
                    for (int currentInt : intsInFile) {
                        if (currentInt >= rangeMin && currentInt <= rangeMax) {
                            updateNumberExistence(currentInt, 1, bitfield);
                        }
                    }
                    break;
                }
            }

            // Now loop through the range and return the integer that is not the bit field
            if (rangeMin != -1) {
                for (int candidate = rangeMin; candidate <= rangeMax; candidate++) {
                    if (!numberExists(candidate, bitfield)) {
                        return candidate;
                    }
                }
            }

            return -1;
        }

        private boolean numberExists(int num, byte[] bitArray) {
            int bytePos = num / 8;
            int posInByte = num < 8 ? num : num % 8;

            byte targetByte = bitArray[bytePos];
            byte mask = (byte) (1 << posInByte);

            return (targetByte & mask) != 0;
        }

        private void updateNumberExistence(int num, int flag, byte[] bitArray) {
            int bytePos = num / 8;
            int posInByte = num < 8 ? num : num % 8;

            byte mask = (byte) ~(1 << posInByte);
            byte targetByte = bitArray[bytePos];
            bitArray[bytePos] = (byte) ((targetByte & mask) | (flag << posInByte));
        }
    }
}
