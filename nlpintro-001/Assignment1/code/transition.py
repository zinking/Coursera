class Transition(object):
    """
    This class defines a set of transitions which are applied to a
    configuration to get the next configuration.
    """
    # Define set of transitions
    LEFT_ARC = 'LEFTARC'
    RIGHT_ARC = 'RIGHTARC'
    SHIFT = 'SHIFT'
    REDUCE = 'REDUCE'

    def __init__(self):
        raise ValueError('Do not construct this object!')
        
    @staticmethod
    def has_head(arcs,i):
        for arc in arcs:
            if arc[2] == i :
                return True
        return False

    @staticmethod
    def left_arc(conf, relation):
        """
            :param configuration: is the current configuration
            :return : A new configuration or -1 if the pre-condition is not satisfied
        """
        """
            arc(b,l,s)
            precondition: s is not artificial node & s does not already have a head
            (sigma|i,j|beta,A) => (sigma,j|beta,A+(j,l,i))
        """

        if not conf.buffer or not conf.stack:
            return -1
        idx_wi = conf.stack[-1]
        arcs = conf.arcs
        if idx_wi ==0 or Transition.has_head(arcs,idx_wi):
            return -1
        idx_wi = conf.stack.pop(-1)
        idx_wj = conf.buffer[0]
        
        conf.arcs.append((idx_wj,relation,idx_wi))

    @staticmethod
    def right_arc(conf, relation):
        """
            :param configuration: is the current configuration
            :return : A new configuration or -1 if the pre-condition is not satisfied
        """
        """
            (sigma|i,j|beta,A) => (sigma|i|j,beta,A+(i,l,j))
        """
        if not conf.buffer or not conf.stack:
            return -1

        # You get this one for free! Use it as an example.

        idx_wi = conf.stack[-1]
        idx_wj = conf.buffer.pop(0)

        conf.stack.append(idx_wj)
        conf.arcs.append((idx_wi, relation, idx_wj))

    @staticmethod
    def reduce(conf):
        """
            :param configuration: is the current configuration
            :return : A new configuration or -1 if the pre-condition is not satisfied
        """
        """
            preconditon top token (i) has a head
            (sigma|i,beta,A) => (sigma,beta,A) 
        """
        idx_wi = conf.stack[-1]
        if Transition.has_head(conf.arcs,idx_wi):
            conf.stack.pop(-1)
        else:
            return -1

    @staticmethod
    def shift(conf):
        """
            :param configuration: is the current configuration
            :return : A new configuration or -1 if the pre-condition is not satisfied
        """
        """
            (sigma, i|beta,A) => (sigma|i,beta,A)
        """
        idx_wj = conf.buffer.pop(0)
        conf.stack.append(idx_wj)
