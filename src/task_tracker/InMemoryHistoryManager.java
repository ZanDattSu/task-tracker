package task_tracker;

import task_tracker.tasks_type.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    class Node {
        public Node prev;
        public Task task;
        public Node next;

        public Node(Task task) {
            this.task = task;
        }
    }

    private Node head = null;
    private Node tail = null;

    private final HashMap<Integer, Node> nodes = new HashMap<>();


    private void linkLast(Task task) {
        Node newNode = new Node(task);
        if (head == null) {
            head = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
        }
        tail = newNode;
        nodes.put(task.getID(), newNode);
    }

    private void removeNode(Node node) {
        remove(node.task.getID());
    }

    @Override
    public List<Task> getHistory() {
        ArrayList<Task> tasks = new ArrayList<>();
        Node current = head;
        while (current != null) {
            tasks.add(current.task);
            current = current.next;
        }
        return tasks;
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        remove(task.getID());
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        Node nodeToRemove = nodes.get(id);
        if (nodeToRemove == null) {
            return;
        }

        if (nodeToRemove.prev == null) {
            head = nodeToRemove.next;
            if (head != null) {
                head.prev = null;
            }
        } else {
            nodeToRemove.prev.next = nodeToRemove.next;
        }

        if (nodeToRemove.next == null) {
            tail = nodeToRemove.prev;
        } else {
            nodeToRemove.next.prev = nodeToRemove.prev;
        }
    }
}
