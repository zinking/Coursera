use std::fmt;
use std::fmt::{Display, Formatter};
use std::marker::PhantomData;
use std::ptr::NonNull;

struct Node<T> {
    val: T,
    next: Option<NonNull<Node<T>>>,
    prev: Option<NonNull<Node<T>>>,
}

impl<T> Node<T> {
    fn new(t: T) -> Node<T> {
        Node {
            val: t,
            prev: None,
            next: None,
        }
    }
}

pub struct LinkedList<T> {
    length: u32,
    head: Option<NonNull<Node<T>>>,
    tail: Option<NonNull<Node<T>>>,
    marker: PhantomData<Box<Node<T>>>
}

impl<T> LinkedList<T> {
    pub fn new() -> Self {
        Self {
            length: 0,
            head: None,
            tail: None,
            marker: PhantomData,
        }
    }

    pub fn insert_head(&mut self, obj: T) {
        let mut node = Box::new(Node::new(obj));
        node.next = self.head;
        node.prev = None;
        let node_ptr = Some(unsafe {NonNull::new_unchecked(Box::into_raw(node))});
        match self.head {
            None => self.tail = node_ptr,
            Some(head_ptr) => unsafe { (*head_ptr.as_ptr()).prev = node_ptr}
        }
        self.head = node_ptr;
        self.length += 1;
    }

    pub fn insert_tail(&mut self, obj: T) {
        let mut node = Box::new(Node::new(obj));
        node.next = None;
        node.prev = self.tail;
        let node_ptr = Some(unsafe {NonNull::new_unchecked(Box::into_raw(node))});
        match self.tail {
            None => self.head = node_ptr,
            Some(tail_ptr) => unsafe { (*tail_ptr.as_ptr()).next = node_ptr}
        }
        self.tail = node_ptr;
        self.length += 1;
    }

    pub fn insert(&mut self, i: u32, obj: T) {
        let len = self.length;
        if len < i {
            panic!("index out of bounds")
        }

        if i == 0 || self.head.is_none() {
            self.insert_head(obj);
            return;
        }

        if len == i {
            self.insert_tail(obj);
            return;
        }

        if let Some(mut ith_node) = self.head {
            for _ in 0..i {
                unsafe {
                    match (*ith_node.as_ptr()).next {
                        None => panic!("Index out of bounds"),
                        Some(next_ptr) => ith_node = next_ptr,
                    }
                }
            }

            let mut node = Box::new(Node::new(obj));
            unsafe {
                node.prev = (*ith_node.as_ptr()).prev;
                node.next = Some(ith_node);
                if let Some(p) = (*ith_node.as_ptr()).prev {
                    let node_ptr = Some(NonNull::new_unchecked(Box::into_raw(node)));
                    println!("{:?}", (*p.as_ptr()).next);
                    (*p.as_ptr()).next = node_ptr;
                    (*ith_node.as_ptr()).prev = node_ptr;
                }
            }
        }
    }

    pub fn delete_head(&mut self) -> Option<T> {
        self.head.map(|head_ptr| unsafe {
            let old_head = Box::from_raw(head_ptr.as_ptr());
            match old_head.next {
                Some(mut next_ptr) => next_ptr.as_mut().prev = None,
                None => self.tail = None,
            }
            self.head = old_head.next;
            self.length -= 1;
            old_head.val
        })
    }

    pub fn delete_tail(&mut self) -> Option<T> {
        self.tail.map(|tail_ptr| unsafe {
            let old_tail = Box::from_raw(tail_ptr.as_ptr());
            match old_tail.prev {
                Some(mut prev_ptr) => prev_ptr.as_mut().prev = None,
                None => self.head = None,
            }
            self.tail = old_tail.prev;
            self.length -= 1;
            old_tail.val
        })
    }

    pub fn delete(&mut self, i: u32) -> Option<T> {
        let len = self.length;
        if len < i {
            panic!("index out of bounds")
        }

        if i == 0 || self.head.is_none() {
            return self.delete_head()
        }

        if len == i {
            return self.delete_tail()
        }

        if let Some(mut ith_node) = self.head {
            for _ in 0..i {
                unsafe {
                    match (*ith_node.as_ptr()).next {
                        None => panic!("index out of bounds"),
                        Some(next_ptr) => ith_node = next_ptr,
                    }
                }
            }

            unsafe {
                let old_ith = Box::from_raw(ith_node.as_ptr());
                if let Some(mut prev) = old_ith.prev {
                    prev.as_mut().next = old_ith.next;
                }
                if let Some(mut next) = old_ith.next {
                    next.as_mut().prev = old_ith.prev;
                }
                self.length -= 1;
                Some(old_ith.val)
            }
        } else {
            None
        }

    }

    pub fn get(&self, i: u32) -> Option<&'static T> {
        Self::get_ith_node(self.head, i)
    }

    fn get_ith_node(node: Option<NonNull<Node<T>>>, i: u32) -> Option<&'static T> {
        match node {
            None => None,
            Some(next_ptr) => match i {
                0 => Some(unsafe { &(*next_ptr.as_ptr()).val}),
                _ => Self::get_ith_node(unsafe {(*next_ptr.as_ptr()).next}, i - 1),
            }
        }
    }
}

impl<T> Drop for LinkedList<T> {
    fn drop(&mut self) {
        // while self.delete_head().is_some() {}
        while self.length > 0 {
            self.delete_head();
        }
    }
}

impl<T> Display for LinkedList<T>
where T: Display,
{
    fn fmt(&self, f: &mut Formatter) -> fmt::Result {
        match self.head {
            Some(node) => write!(f, "{}", unsafe { node.as_ref() }),
            None => Ok(()),
        }
    }
}

impl<T> Display for Node<T>
where T: Display,
{
    fn fmt(&self, f: &mut Formatter) -> fmt::Result {
        match self.next {
            Some(node) => write!(f, "{}, {}", self.val, unsafe { node.as_ref() }),
            None => write!(f, "{}", self.val)
        }
    }
}

#[cfg(test)]
mod tests {
    use crate::ds::linked_list::LinkedList;

    #[test]
    fn insert_tail_works() {
        let mut list = LinkedList::<i32>::new();
        let second_value = 2;
        list.insert_tail(1);
        list.insert_tail(second_value);
        println!("linkedlist is {}", list);
        match list.get(1) {
            Some(val) => assert_eq!(*val, second_value),
            None => panic!("Expected to find {} at index 1", second_value),
        }
    }

    #[test]
    fn insert_head_works() {
        let mut list = LinkedList::<i32>::new();
        let second_value = 2;
        list.insert_head(1);
        list.insert_head(second_value);
        println!("linkedlist is {}", list);
        match list.get(1) {
            Some(val) => assert_eq!(*val, 1),
            None => panic!("Expected to find {} at index 1", second_value),
        }
    }

    #[test]
    fn insert_works() {
        let mut list = LinkedList::<i32>::new();
        list.insert(0, 1);
        list.insert(1, 2);
        list.insert(1, 3);
        assert_eq!(list.get(1), Some(&3));
    }

    #[test]
    fn delete_works() {
        let mut list = LinkedList::<i32>::new();
        list.insert(0, 1);
        list.insert(1, 2);
        list.insert(1, 3);

        list.delete(2);
        list.delete(0);
        assert_eq!(list.get(0), Some(&3));
    }
}